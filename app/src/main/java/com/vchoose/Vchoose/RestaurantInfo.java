package com.vchoose.Vchoose;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
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

    ArrayList<HashMap<String, HashMap>> restaurantInfoJsonlist = new ArrayList<HashMap<String, HashMap>>();
    String restaurantID;
    ListView list1;
    ListView list2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restraunt_info);

        Bundle extras = getIntent().getExtras();
        //ArrayList<String> stringList = extras.getStringArrayList("RestrauntInfo");
        TextView textview = (TextView)findViewById(R.id.restruant_id);
        TextView textview2=(TextView)findViewById(R.id.location);
        TextView textview3=(TextView)findViewById(R.id.DishPhone);
        list1 = (ListView)findViewById(R.id.list1);
        list2 = (ListView)findViewById(R.id.list2);
        restaurantID = extras.getString("restaurant_id");
        textview.setText(restaurantID);
        textview.setText(extras.getString("restaurant_name"));
        textview2.setText(extras.getString("restaurant_phone"));
        textview3.setText(extras.getString("restaurant_location"));
        new ProgressTask(this).execute(restaurantID);
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

            final ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < restaurantInfoJsonlist.size(); i++) {
                String name = (String)restaurantInfoJsonlist.get(i).get("menu_sections").get("menu_type_name");
                list.add(name);
            }
            final ArrayAdapter adapter1 = new ArrayAdapter(RestaurantInfo.this,
                    android.R.layout.simple_list_item_1, list);

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

            });
        }
        protected Boolean doInBackground(final String... args) {

            VcJsonReader jParser = new VcJsonReader();

            String restaurantID = args[0];

            String response = jParser.getRestaurantInfo(restaurantID);


            try {
                JSONTokener tokener = new JSONTokener(response);
                JSONObject responseObject = (JSONObject) tokener.nextValue();
                JSONArray dataTable = responseObject.getJSONArray("data");
                JSONArray menu_sections = dataTable.getJSONObject(0).getJSONArray("menu_sections");
                restaurantInfoJsonlist.clear();

                Log.v(" menu_sections.length()", String.valueOf( menu_sections.length()));
                for (int i = 0; i < menu_sections.length(); i++) {
                    HashMap<String, HashMap> map1 = new HashMap<String, HashMap>();
                    HashMap<String, String> map2 = new HashMap<String, String>();
                    HashMap<String, ArrayList<String>> map2_dishes = new HashMap<String, ArrayList<String>>();
                    ArrayList<String> dishes = new ArrayList<String>();

                    JSONObject restaurant_menu_type = menu_sections.getJSONObject(i);
                    map2.put("menu_type_name", restaurant_menu_type.getString("name"));
                    JSONArray menu_dishes = restaurant_menu_type.getJSONArray("menu_items");
                    for(int j = 0; j < menu_dishes.length(); j++) {
                        dishes.add(menu_dishes.getJSONObject(j).getString("name"));
                    }
                    map2_dishes.put("dishes",dishes);

                    map1.put("menu_sections",map2);
                    map1.put("menu_items",map2_dishes);
                    restaurantInfoJsonlist.add(map1);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
