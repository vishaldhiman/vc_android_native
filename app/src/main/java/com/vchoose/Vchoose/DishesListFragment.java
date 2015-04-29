package com.vchoose.Vchoose;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.vchoose.Vchoose.com.vchoose.Vchoose.api.calls.SubmitRatings;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This fragment is to show the search result
 */
public class DishesListFragment extends Fragment {
    ArrayList<HashMap<String, String>> jsonlist = new ArrayList<HashMap<String, String>>();
    ListView myList;
    private static final String dishname = "vehicleType";
    private static final String location = "vehicleColor";
    private static final String fuel = "fuel";
    private static final String rating = "rating";
    private static final String description = "description";
    private static final String thumbnail = "thumbnail";

    public static String AuthenticationToken;


    public void setArrayList(ArrayList<HashMap<String, String>> jsonlist ) {
        this.jsonlist = jsonlist;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mylist_fragment, container, false);
        myList=(ListView)v.findViewById(android.R.id.list);
        RatingAdapter adapter = new RatingAdapter(jsonlist);

        myList.setAdapter(adapter);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("clicked", "true");
                Intent intent = new Intent(getActivity(), DishInfo.class);
                HashMap<String, String> dishes = (HashMap<String, String>) jsonlist.get(position);
                String name_text = dishes.get(dishname);
                String description_text = dishes.get(description);
                String location_text = dishes.get(location);
                String dish_id = dishes.get("ID");
                ArrayList<String> stringList = new ArrayList<String>();
                stringList.add(name_text);
                stringList.add(description_text);
                stringList.add(location_text);
                intent.putExtra("DishInfo", stringList);
                intent.putExtra("Dish_id", dish_id);
                intent.putExtra("Authentication", AuthenticationToken);
                intent.putExtra("restaurant_id", dishes.get("restaurant_id"));

                ArrayList<String> tagList = new ArrayList<String>();
                for(int j = 0;  j < 3 ; j++) {
                    String s = dishes.get("Tag"+j);
                    tagList.add(s);
                }
                intent.putExtra("tagList", tagList);
                startActivity(intent);
            }

        });
        return v;
    }

    class RatingAdapter extends ArrayAdapter {
        ArrayList<HashMap<String, String>> jsonlist;

        RatingAdapter(ArrayList list) {
            super(getActivity(), R.layout.list_activity, list);
            jsonlist = list;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            View row=convertView;
            //ViewWrapper wrapper;
            DishViewWrapper wrapper;
            RatingBar rate;
            TextView tag1;
            TextView tag2;
            TextView tag3;

            final HashMap<String, String> cur_dish = (HashMap<String, String>) jsonlist.get(position);

            /*if (row==null)*/ {
                LayoutInflater inflater=getActivity().getLayoutInflater();
                row=inflater.inflate(R.layout.list_activity, parent, false);//set the list view

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
                wrapper=new DishViewWrapper(row);
                row.setTag(wrapper);
                rate=wrapper.getRatingBar();
                rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                                      @Override
                                                      public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                          if(fromUser) {
                              Log.v("Rating Bar changed", String.valueOf(rating));
                              Log.v("The dish of Rating bar", String.valueOf(position));
                              Log.v("ID", cur_dish.get("ID"));

                              //int menu_item_id = Integer.parseInt(cur_dish.get("ID"));

                              //jParser.submitRatingForDish(menu_item_id,(new Float(rating)).intValue());

                              //new ProgressTask(MainActivity.this).execute(locationEdit.getText().toString(), keyword, radius);
                              new SubmitRatings(getActivity()).execute(cur_dish.get("ID"),String.valueOf(rating));

                              //the Rating is stored here
                          }
                      }
                  }
                );
            }/*
            else {
                wrapper=(DishViewWrapper)row.getTag();
                rate=wrapper.getRatingBar();
            }
            */

            //RowModel model=getModel(position);

            wrapper.getVehicleType().setText(cur_dish.get(dishname));
            wrapper.getVehicleColor().setText(cur_dish.get(location));
            rate.setTag(new Integer(position));
            rate.setRating(Float.parseFloat(cur_dish.get(rating)));
            wrapper.getDescription().setText(cur_dish.get(description));


                String thumbnail_url = cur_dish.get(thumbnail);

                if ((thumbnail_url != null) && (!thumbnail_url.equalsIgnoreCase("null"))) {
                    //url = new URL("http://vchoose.us"+thumbnail_url);

                    //Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    //wrapper.getDishImage().setImageBitmap(bmp);

                    new DownloadImageTask(wrapper.getDishImage()).execute("http://vchoose.us"+thumbnail_url);
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

    public void setAuthenticationToken(String authenticationToken) {
        AuthenticationToken = authenticationToken;
    }
}
