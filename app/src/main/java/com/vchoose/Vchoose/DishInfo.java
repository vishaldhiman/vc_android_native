package com.vchoose.Vchoose;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vchoose.Vchoose.util.VcJsonReader;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;


public class DishInfo extends ActionBarActivity {

    private String Authentication;
    private String dish_id;
    private String restaurant_id;
    private String restaurant_name;
    private String restaurant_phone;
    private String provider;
    private String provider_name;
    private String restaurant_location;
    private String url_tag = "http://vchoose.us/tag_assignments.json";

    private TextView tag1;
    private TextView tag2;
    private TextView tag3;
    private String m_Text = "";
    private String tag[];

    //public static ArrayList<String> stringList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_info);
        Bundle extras = getIntent().getExtras();
        ArrayList<String> stringList = extras.getStringArrayList("DishInfo");
        Authentication = extras.getString("Authentication");
        restaurant_id = extras.getString("restaurant_id");
        restaurant_name = extras.getString("restaurant_name");
        restaurant_phone = extras.getString("restaurant_phone");
        restaurant_location = extras.getString("restaurant_location");
        provider = extras.getString("provider");

        //Authentication = "hG4T5oT96uwzDYbxpnST";      //for test

        dish_id = extras.getString("Dish_id");
        ArrayList<String> tagList = extras.getStringArrayList("tagList");

        TextView textview = (TextView)findViewById(R.id.DishName);
        TextView textview2=(TextView)findViewById(R.id.DishPhone);
        Button restaurantName = (Button)findViewById(R.id.go_to_restaurant);
        TextView textview3=(TextView)findViewById(R.id.DishDiscribe);
        TextView customizedTag = (TextView)findViewById(R.id.customizeTag);
        tag1 = (TextView)findViewById(R.id.tag_info1);
        tag2 = (TextView)findViewById(R.id.tag_info2);
        tag3 = (TextView)findViewById(R.id.tag_info3);

        if (provider.equals("user_added")){
            String provider_name = extras.getString("provider_name");
            customizedTag.setText("Customized dish by " + provider_name);
            RelativeLayout background = (RelativeLayout)findViewById(R.id.dishInfo_background);
            background.setBackgroundColor(Color.rgb(149, 223, 191));
        }

        textview.setText(stringList.get(0));
        //textview2.setText(stringList.get(2));
        restaurantName.setText(stringList.get(2));
        textview3.setText(unescape(stringList.get(1)));
        textview3.setMovementMethod(new ScrollingMovementMethod());

        tag = new String[3];
        tag[0] = tagList.get(0);
        tag[1] = tagList.get(1);
        tag[2] = tagList.get(2);
        tagDisplay(tag);

        restaurantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                Intent intent = new Intent(getApplicationContext(),RestaurantInfo.class);
                intent.putExtra("restaurant_id",restaurant_id);
                intent.putExtra("restaurant_name",restaurant_name);
                intent.putExtra("restaurant_phone",restaurant_phone);
                intent.putExtra("restaurant_location",restaurant_location);
                startActivity(intent);
            }
        });
    }

    private String unescape(String description) {
        return description.replaceAll("\\\\n", "\\\n");
    }

    private void tagDisplay(String[] s) {
        if(s[0] != null) {
            tag3.setText(s[0]);
        }
        else
            tag3.setVisibility(View.GONE);

        if(s[1] != null)
            tag2.setText(s[1]);
        else
            tag2.setVisibility(View.GONE);

        if(s[2] != null)
            tag1.setText(s[2]);
        else
            tag1.setVisibility(View.GONE);
    }

    public void customize(View view) {
        Intent intent = new Intent(this, Customization.class);
        startActivity(intent);
    }

    public void addTag(View view) {
        if(Authentication != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Your tag");

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();

                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            post(m_Text, dish_id);
                        }
                    });
                    t.start();
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                    }
                    tag[2] = tag[1];
                    tag[1] = tag[0];
                    tag[0] = m_Text;
                    tagDisplay(tag);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dish_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void post(String tag, String id) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url_tag);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                /*hard coded for testing*/
            nameValuePairs.add(new BasicNameValuePair("tag_name", tag));
            nameValuePairs.add(new BasicNameValuePair("taggable_type", "MenuItem"));
            nameValuePairs.add(new BasicNameValuePair("taggable_id", id));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpPost.addHeader("authentication_token",Authentication);
            HttpResponse response = client.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
