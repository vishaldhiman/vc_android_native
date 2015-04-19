package com.vchoose.Vchoose;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    public static String NAME = "";
    public static String DIS = "";
    private String Authentication;
    private String dish_id;
    private String url_tag = "http://vchoose.us/tag_assignments.json";

    //public static ArrayList<String> stringList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_info);
        Bundle extras = getIntent().getExtras();
        ArrayList<String> stringList = extras.getStringArrayList("DishInfo");
        Authentication = extras.getString("Authentication");
        dish_id = extras.getString("Dish_id");
        TextView textview = (TextView)findViewById(R.id.DishName);
        TextView textview2=(TextView)findViewById(R.id.DishPhone);
        TextView textview3=(TextView)findViewById(R.id.DishDiscribe);

        textview.setText(stringList.get(0));
        textview2.setText(stringList.get(2));
        textview3.setText(stringList.get(1));
    }

    public void customize(View view) {
        Intent intent = new Intent(this, Customization.class);
        startActivity(intent);
    }

    public void addTag(View view) {
        if(Authentication != null) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    post("dd", dish_id);
                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
            }
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
