package com.vchoose.Vchoose;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DishInfo extends ActionBarActivity {
    private static final String TAG = "SamT_";

    /* keywords for dish */
    private static final String dishname = "dishName";
    private static final String dishID = "ID";
    private static final String location = "location";
    private static final String price = "price";
    private static final String rating = "rating";
    private static final String description = "description";
    private static final String dishTag = "Tag";
    private static final String provider = "provider";
    private static final String provider_name = "provider_name";
    private static final String thumbnail = "thumbnail";
    private static final String reviews = "reviews";
    private static final String review = "review";
    private static final String reviewer = "reviewer";
    private static final String reviewerImage = "image";
    private static final String reviewRating = "rating";
    //new value to pass to DishInfo
    private static final String dishInfo = "DishInfo";
    private static final String dishTagList = "tagList";

    /* keywords for restaurant information from dish & also keywords for restaurant info */
    private static final String dishRestID = "restaurant_id";
    private static final String dishRestName = "restaurant_name";
    private static final String dishRestPhone = "restaurant_phone";
    private static final String dishRestLocation = "restaurant_location";
    private static final String dishRestLatitude = "latitude";
    private static final String dishRestLongitude = "longitude";

    /* keywords for adding a tag */
    private static final String tag_name = "tag_name";
    private static final String taggableType = "taggable_type";
    private static final String taggableId =  "taggable_id";


    private String Authentication;
    private String dish_id;
    private String restaurant_id;
    private String restaurant_name;
    private String restaurant_phone;
    private String dishProvider;
    private String dishProvider_name;
    private String restaurant_location;
    private String url_tag = "http://vchoose.us/tag_assignments.json";

    private TextView tag1;
    private TextView tag2;
    private TextView tag3;
    private String m_Text;
    private String tag[];

    //public static ArrayList<String> stringList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_dish_info);
        //ActionBar actionBar = getSupportActionBar();
       // actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        final ArrayList<String> stringList = extras.getStringArrayList(dishInfo);
        ArrayList reviews = extras.getParcelableArrayList(DishInfo.reviews);
        //HashMap<String, String> s = (HashMap)arrayList.get(0);
        //ArrayList<HashMap<String, String>> reviews = (ArrayList<HashMap<String, String>>)extras.getParcelableArrayList(DishInfo.reviews);
        Authentication = extras.getString("Authentication");
        restaurant_id = extras.getString(dishRestID);
        restaurant_name = extras.getString(dishRestName);
        restaurant_phone = extras.getString(dishRestPhone);
        restaurant_location = extras.getString(dishRestLocation);
        dishProvider = extras.getString(provider);

        //Authentication = "hG4T5oT96uwzDYbxpnST";      //for test

        dish_id = extras.getString(dishID);
        ArrayList<String> tagList = extras.getStringArrayList(dishTagList);

        TextView textview = (TextView)findViewById(R.id.DishName);
        TextView textview2=(TextView)findViewById(R.id.DishPhone);
        TextView dishLocation = (TextView)findViewById(R.id.dishLocation);
        Button restaurantName = (Button)findViewById(R.id.go_to_restaurant);
        Button navigator = (Button)findViewById(R.id.restaurant_direction);
        TextView textview3=(TextView)findViewById(R.id.DishDiscribe);
        TextView customizedTag = (TextView)findViewById(R.id.customizeTag);
        ListView reviewList = (ListView)findViewById(R.id.reviews);
        tag1 = (TextView)findViewById(R.id.tag_info1);
        tag2 = (TextView)findViewById(R.id.tag_info2);
        tag3 = (TextView)findViewById(R.id.tag_info3);

        if (dishProvider.equals("user_added")){
            String provider_name = extras.getString(DishInfo.provider_name);
            customizedTag.setText("Customized dish by " + provider_name);
            RelativeLayout background = (RelativeLayout)findViewById(R.id.dishInfo_background);
            background.setBackgroundColor(Color.rgb(149, 223, 191));
        } else {
            customizedTag.setHeight(0);
        }

        reviewList.setAdapter(new reviewArrayAdapter(reviews));
        /*
        HashMap<String,String> dishReview = (HashMap) reviews.get(0);
        HashMap<String,String> dishReview2 = (HashMap) reviews.get(1);
        Log.v(TAG + "review", dishReview.get(review));
        Log.v(TAG + "review2", dishReview2.get(review));
        */

        textview.setText(stringList.get(0));
        //textview2.setText(stringList.get(2));
        final String restName = stringList.get(2);
        restaurantName.setText(restName);
        dishLocation.setText(restaurant_location);

        /* Navigation */
        final String latitude = extras.getString(dishRestLatitude);
        final String longitude = extras.getString(dishRestLongitude);

        navigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                //start navigation directly
                Uri gmmIntentUri = Uri.parse("google.navigation:q=34.021156,-118.299918");//latitude,longitude
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                */

                //pin the point first
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + latitude + "," + longitude + "(" + restName + ")");
                Log.v(TAG + "gmmIntentUri", "geo:0,0?q=" + latitude + "," + longitude + "(" + restName + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
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
                intent.putExtra(dishRestID,restaurant_id);
                intent.putExtra(dishRestName,restaurant_name);
                intent.putExtra(dishRestPhone,restaurant_phone);
                intent.putExtra(dishRestLocation,restaurant_location);
                startActivity(intent);
            }
        });

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .build();
        ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
        shareButton.setShareContent(content);
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

        switch (id) {
            case R.id.home:
                finish();
                break;
            case R.id.action_settings:
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    public void post(String tag, String id) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url_tag);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                /*hard coded for testing*/
            nameValuePairs.add(new BasicNameValuePair(tag_name, tag));
            nameValuePairs.add(new BasicNameValuePair(taggableType, "MenuItem"));
            nameValuePairs.add(new BasicNameValuePair(taggableId, id));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpPost.addHeader("authentication_token",Authentication);
            HttpResponse response = client.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class reviewArrayAdapter extends ArrayAdapter {
        ArrayList jsonlist;
        reviewArrayAdapter(ArrayList list) {
            super(DishInfo.this, R.layout.dish_list_componet, list);
            jsonlist = list;
            if(jsonlist.size() == 0) {
                ListView listView = (ListView)findViewById(R.id.reviews);
                listView.setBackground(null);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            LayoutInflater inflater=DishInfo.this.getLayoutInflater();
            row=inflater.inflate(R.layout.dish_review_component, parent, false);

            TextView review_text = (TextView)row.findViewById(R.id.review_words);
            TextView review_writer = (TextView)row.findViewById(R.id.review_writer);
            RatingBar review_rating = (RatingBar)row.findViewById(R.id.review_rating);

            HashMap<String,String> dishReview = (HashMap) jsonlist.get(position);
            review_writer.setText(dishReview.get(reviewer));
            review_text.setText(dishReview.get(review));
            review_rating.setRating(Float.valueOf(dishReview.get(reviewRating)));

            ImageView imageView = (ImageView)row.findViewById(R.id.review_image);
            imageView.setImageResource(R.drawable.blank_user);

            Log.v(TAG + "reviewerImage", dishReview.get(reviewerImage));

            if(!dishReview.get(reviewerImage).equals("null")) {
                new DownloadImageTask(imageView).execute(dishReview.get(reviewerImage));
            }
            /*Bitmap icon = BitmapFactory.decodeResource(DishInfo.this.getResources(),
                    R.drawable.blank_user);
            ImageView imageView = (ImageView)row.findViewById(R.id.review_image);
            imageView.setImageBitmap(icon);
            */

            //Log.v(TAG + "ImageWidth", String.valueOf(imageView.getWidth()));
            return row;
        }
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            /*
            Log.v(TAG + "ImageWidth", String.valueOf(mIcon11.getWidth()));
            Log.v(TAG + "ImageHeight", String.valueOf(mIcon11.getHeight()));
            */
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
