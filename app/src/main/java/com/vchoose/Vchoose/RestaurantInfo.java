package com.vchoose.Vchoose;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.vchoose.Vchoose.util.DownloadImageTask;
import com.vchoose.Vchoose.util.VcJsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Archie on 04/06/2015.
 */
public class RestaurantInfo extends Activity {
    private static final String TAG = "XTest_";

    /* keywords for restaurant information from last page */
    private static final String dishRestID = "restaurant_id";
    private static final String dishRestName = "restaurant_name";
    private static final String dishRestPhone = "restaurant_phone";
    private static final String dishRestLocation = "restaurant_location";

    private static final String review = "review";
    private static final String reviewer = "reviewer";
    private static final String reviewerImage = "image";
    private static final String reviewRating = "rating";

    JSONArray menu_sections;

    //ArrayList<HashMap<String, HashMap>> restaurantInfoJsonlist = new ArrayList<HashMap<String, HashMap>>();
    String restaurantID;
    ListView dishList;
    Spinner menu_spinner;
    LinearLayout dishInfoLinearLayout;
    static int lastSelectedDishPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restraunt_info);
        dishInfoLinearLayout = (LinearLayout)findViewById(R.id.dishInfo);
        dishInfoLinearLayout.setVisibility(View.INVISIBLE);

        Bundle extras = getIntent().getExtras();
        //ArrayList<String> stringList = extras.getStringArrayList("RestrauntInfo");
        TextView textview = (TextView)findViewById(R.id.restruant_id);
        TextView textview2=(TextView)findViewById(R.id.location);
        TextView textview3=(TextView)findViewById(R.id.DishPhone);
        dishList = (ListView)findViewById(R.id.dishes);
        menu_spinner = (Spinner)findViewById(R.id.menu_section);
        restaurantID = extras.getString(dishRestID);

        textview.setText(restaurantID);
        textview.setText(extras.getString(dishRestName));
        textview2.setText(extras.getString(dishRestPhone));
        textview3.setText(extras.getString(dishRestLocation));

        new ProgressTask(this).execute(restaurantID);
    }

    @Override
    protected void onResume() {
        //lastSelectedDishPosition = -1;
        super.onResume();
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    private class ProgressTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog dialog;

        public ProgressTask(Activity activity) {
            context = activity;
            dialog = new ProgressDialog(context);
        }
        private Context context;

        protected void onPreExecute() {
            this.dialog.setMessage("Searching Restaurant Information");
            this.dialog.show();
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            final ArrayList<String> list_menu_type = new ArrayList<String>();
            if(menu_sections != null) {
                for (int i = 0; i < menu_sections.length(); i++) {
                    try {
                        String name = menu_sections.getJSONObject(i).getString("name");
                        list_menu_type.add(name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            final ArrayAdapter adapter1 = new ArrayAdapter(RestaurantInfo.this,
                    android.R.layout.simple_spinner_item, list_menu_type);


            menu_spinner.setAdapter(adapter1);
            menu_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                    try {
                        JSONArray list_dishes = menu_sections.getJSONObject(position).getJSONArray("menu_items");

                        ArrayList<String> dishes = new ArrayList<String>();
                        if (list_dishes != null) {
                            for (int i = 0; i < list_dishes.length(); i++) {
                                dishes.add(list_dishes.getJSONObject(i).getString("name"));
                            }
                        }

                        // ArrayList<String> dishes = (ArrayList<String>) restaurantInfoJsonlist.get(position).get("menu_items").get("dishes");
                        //Log.v("OnClick",String.valueOf(dishes.size()));

                        lastSelectedDishPosition = -1;
                        //dishList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                        dishList.setAdapter(new MyArrayAdapter(RestaurantInfo.this,
                                android.R.layout.simple_list_item_1, dishes));

                        dishList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position_dish, long id) {
                                Log.v(TAG + "ItemSelected", String.valueOf(position_dish));

                                if((lastSelectedDishPosition != -1)&& (lastSelectedDishPosition != position_dish)) {
                                    int num_of_visible_view=parent.getLastVisiblePosition() -
                                            parent.getFirstVisiblePosition();
                                    for (int i = 0; i < num_of_visible_view; i++) {
                                        // do your code here
                                        parent.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.backgroundColor));
                                    }
                                }

                                view.setBackgroundColor(getResources().getColor(R.color.backgroundDarkColor));

                                lastSelectedDishPosition = position_dish;


                                Log.v(TAG + "click", "position_dish=" + String.valueOf(position_dish));
                                Log.v(TAG + "last", "lastSelectedDishPosition=" + String.valueOf(lastSelectedDishPosition));

                                try {
                                    dishInfoLinearLayout.setVisibility(View.VISIBLE);
                                    String dishName = menu_sections.getJSONObject(position).getJSONArray("menu_items").getJSONObject(position_dish).getString("name");
                                    String dishImage = menu_sections.getJSONObject(position).getJSONArray("menu_items").getJSONObject(position_dish).getString("thumbnail");
                                    String dishPrice = menu_sections.getJSONObject(position).getJSONArray("menu_items").getJSONObject(position_dish).getJSONObject("price").getString("dollars");
                                    TextView dishN = (TextView) findViewById(R.id.DishName);
                                    TextView dishD = (TextView) findViewById(R.id.DishDiscribe);
                                    TextView dishP = (TextView) findViewById(R.id.dishPrice);
                                    ImageView dishPic = (ImageView) findViewById(R.id.icon);
                                    ListView reviewList = (ListView) findViewById(R.id.reviews);

                                    dishN.setText(dishName);
                                    dishD.setText(menu_sections.getJSONObject(position).getJSONArray("menu_items").getJSONObject(position_dish).getString("description"));
                                    if(!dishPrice.equals("null"))
                                        dishP.setText(dishPrice);
                                    else
                                        dishP.setText(null);
                                    if(!dishImage.equals("null"))
                                        new DownloadImageTask(dishPic).execute("https://vchoose.us" + dishImage);
                                    else
                                        dishPic.setImageResource(R.mipmap.up_load);

                                    ArrayList<HashMap<String, String>> reviews = new ArrayList<HashMap<String, String>>();
                                    JSONArray reviewItems = menu_sections.getJSONObject(position).getJSONArray("menu_items").getJSONObject(position_dish).getJSONArray("reviews");
                                    int reviewNum = reviewItems.length();
                                    for (int i = 0; i < reviewNum; i++) {
                                        HashMap<String, String> temp = new HashMap<String, String>();
                                        String review = reviewItems.getJSONObject(i).getString("review");
                                        String reviewer = reviewItems.getJSONObject(i).getJSONObject("reviewer").getString("username");
                                        String reviewerPic = reviewItems.getJSONObject(i).getJSONObject("reviewer").getString("image");
                                        String rating = reviewItems.getJSONObject(i).getString("rating");
                                        temp.put(RestaurantInfo.review, review);
                                        temp.put(RestaurantInfo.reviewer, reviewer);
                                        temp.put(RestaurantInfo.reviewerImage, reviewerPic);
                                        temp.put(RestaurantInfo.reviewRating, rating);
                                        reviews.add(temp);
                                    }
                                    if(reviewNum != 0)
                                        reviewList.setAdapter(new reviewArrayAdapter(reviews));
                                    else
                                        reviewList.setVisibility(View.INVISIBLE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            /*
            list1.setAdapter(adapter1);
            list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    ArrayList<String> dishes = (ArrayList<String>)restaurantInfoJsonlist.get(position).get("menu_items").get("dishes");
                    //Log.v("OnClick",String.valueOf(dishes.size()));
                    final StableArrayAdapter adapter2 = new StableArrayAdapter(RestaurantInfo.this,
                            android.R.layout.simple_list_item_1, dishes);
                    list2.setAdapter(adapter2);
                }

            });*/
            }
        }
        protected Boolean doInBackground(final String... args) {

            VcJsonReader jParser = new VcJsonReader();

            String restaurantID = args[0];

            String response = jParser.getRestaurantInfo(restaurantID);


            try {
                JSONTokener tokener = new JSONTokener(response);
                JSONObject responseObject = (JSONObject) tokener.nextValue();
                JSONArray dataTable = responseObject.getJSONArray("data");
                menu_sections = dataTable.getJSONObject(0).getJSONArray("menu_sections");

            }catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class MyArrayAdapter extends ArrayAdapter {
        public MyArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View renderer = super.getView(position, convertView, parent);
            renderer.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
            if(position == RestaurantInfo.lastSelectedDishPosition)
                renderer.setBackgroundColor(getResources().getColor(R.color.backgroundDarkColor));
            return renderer;
        }
    }


    class reviewArrayAdapter extends ArrayAdapter {
        ArrayList jsonlist;
        reviewArrayAdapter(ArrayList list) {
            super(getApplicationContext(), R.layout.dish_review_component_small, list);
            jsonlist = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            LayoutInflater inflater=RestaurantInfo.this.getLayoutInflater();
            row=inflater.inflate(R.layout.dish_review_component_small, parent, false);

            HashMap<String,String> dishReview = (HashMap) jsonlist.get(position);

            TextView review_text = (TextView)row.findViewById(R.id.review_words);
            TextView review_writer = (TextView)row.findViewById(R.id.review_writer);
            RatingBar review_rating = (RatingBar)row.findViewById(R.id.review_rating);

            review_writer.setText(dishReview.get(RestaurantInfo.reviewer));
            review_text.setText(dishReview.get(RestaurantInfo.review));
            review_rating.setRating(Float.valueOf(dishReview.get(RestaurantInfo.reviewRating)));

            ImageView imageView = (ImageView)row.findViewById(R.id.review_image);
            imageView.setImageResource(R.drawable.blank_user);


            if(!dishReview.get(reviewerImage).equals("null")) {
                new DownloadImageTask(imageView).execute(dishReview.get(reviewerImage));
            }

            return row;
        }
    }
}
