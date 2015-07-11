package com.vchoose.Vchoose;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sam on 4/29/2015.
 */
public class RestaurantListFragment extends Fragment {
    private static final String restaurantID = "id";
    private static final String restaurantName = "name";
    private static final String restaurantLocation = "location";
    private static final String restaurantDistance = "distance";
    private static final String restaurantRating = "rating";
    private static final String restaurantDescription = "description";
    private static final String restaurantPhone = "phone";
    private static final String getRestaurantRatingImageUrl = "ratingImage";

    /* keywords for restaurant information */
    private static final String dishRestID = "restaurant_id";
    private static final String dishRestName = "restaurant_name";
    private static final String dishRestPhone = "restaurant_phone";
    private static final String dishRestLocation = "restaurant_location";

    ArrayList<HashMap<String, String>> jsonlist = new ArrayList<HashMap<String, String>>();

    ListView myList;

    public void setArrayList(ArrayList<HashMap<String, String>> jsonlist ) {
        this.jsonlist = jsonlist;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mylist_fragment, container, false);
        myList=(ListView)v.findViewById(android.R.id.list);
        RatingAdapter adapter = new RatingAdapter(jsonlist);

        myList.setAdapter(adapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), RestaurantInfo.class);

                intent.putExtra(dishRestID, jsonlist.get(position).get(restaurantID));
                intent.putExtra(dishRestName, jsonlist.get(position).get(restaurantName));
                intent.putExtra(dishRestPhone, jsonlist.get(position).get(restaurantPhone));
                intent.putExtra(dishRestLocation, jsonlist.get(position).get(restaurantLocation));
                startActivity(intent);
            }
        });
        return v;
    }

    class RatingAdapter extends ArrayAdapter {
        ArrayList<HashMap<String, String>> jsonlist;

        RatingAdapter(ArrayList list) {
            super(getActivity(), R.layout.dish_list_componet, list);
            jsonlist = list;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            View row;
            TextView tag1;
            TextView tag2;
            TextView tag3;
            TextView name, location, distence, phoneText;
            ImageView rating;

            final HashMap<String, String> cur_dish = (HashMap<String, String>) jsonlist.get(position);


            LayoutInflater inflater=getActivity().getLayoutInflater();
            row=inflater.inflate(R.layout.restaurant_list_component, parent, false);//set the list view
            name = (TextView) row.findViewById(R.id.dishName);
            location = (TextView) row.findViewById(R.id.restaurantLocation);
            distence = (TextView) row.findViewById(R.id.restaurantDescription);
            rating = (ImageView) row.findViewById(R.id.ratingPic);
            phoneText = (TextView) row.findViewById(R.id.restaurantPhoneNumber);

            {
                tag1 = (TextView)row.findViewById(R.id.tag1);
                tag2 = (TextView)row.findViewById(R.id.tag2);
                tag3 = (TextView)row.findViewById(R.id.tag3);
                tag1.setText(null);
                tag2.setText(null);
                tag3.setText(null);
                String s[] = new String[3];

                for(int i = 0; i < 3; i++) {
                    s[i] = cur_dish.get("Tag" + i);
                }
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

            name.setText(cur_dish.get(restaurantName));
            location.setText(cur_dish.get(restaurantLocation));
            distence.setText(cur_dish.get(restaurantDistance));
            if(!cur_dish.get("phone").equals("null")) {
                phoneText.setText(cur_dish.get("phone"));
                phoneText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL , Uri.parse("tel:" + cur_dish.get("phone")));
                        startActivity(intent);
                    }
                });
            } else {
                phoneText.setHeight(0);
            }

            String thumbnail_url = cur_dish.get(getRestaurantRatingImageUrl);
            if ((thumbnail_url != null) && (!thumbnail_url.equalsIgnoreCase("null"))) {
                if(rating == null)
                    Log.v("Rating", "is null");
                new DownloadImageTask(rating).execute(thumbnail_url);
                rating.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent("android.intent.action.VIEW",
                                Uri.parse(cur_dish.get("yelp_mobile_url")));
                        startActivity(browserIntent);
                    }
                });
            } else {
                    //clear out the Yelp Logo since we do not have Yelp information for this restaurant
                ImageView yelp_logo = (ImageView) row.findViewById(R.id.sign);
                yelp_logo.invalidate();
                rating.invalidate();
                LinearLayout ratingHolder = (LinearLayout)row.findViewById(R.id.ratingHolder);
                ratingHolder.removeAllViews();
                //ratingHolder.setLayoutParams(new FrameLayout.LayoutParams(0,0));
                //yelp_logo.setImageBitmap(null);
            }
            return(row);
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
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
