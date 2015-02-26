package com.vchoose.searchdishes;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.vchoose.searchdishes.util.VcJsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    private Context context;
    private static String url = "http://docs.blackberry.com/sampledata.json";

    private static final String type = "vehicleType";
    private static final String color = "vehicleColor";
    private static final String fuel = "fuel";
    private static final String rating = "rating";
    private static final String description = "description";

    EditText mEdit;
    EditText locationEdit;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    Spinner spinner;
    RatingBar ratingBar;

    RatingAdapter ratingAdapter;

    ArrayList<HashMap<String, String>> jsonlist = new ArrayList<HashMap<String, String>>();

    ListView lv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_gridlayout);
        mEdit   = (EditText)findViewById(R.id.editText);
        locationEdit = (EditText)findViewById(R.id.editTextLocation);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        Log.i("MyActivity","inside onCreate");
        //new ProgressTask(MainActivity.this).execute();

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.radius_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("MainActivity","Inside onConnectionSuspended "+i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.v("MainActivity","Inside onConnectionFailed "+result.toString());
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        /*
        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void doSearch(View view) {
        String keyword = mEdit.getText().toString();
        Log.i("MyActivity","inside doSearch");
        Log.v("MyActivity","inside doSearch. keyword: "+keyword);
        //this.me

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        try {
            //String json_url = buildUrl("Highland Park, Pittsburgh", keyword, "3");

            //clear the data before downloading again
            jsonlist.clear();

            String radiusString = spinner.getSelectedItem().toString();

            String radius = radiusString.split(" ")[0];     //from '0.5 mi' extract 0.5

            if (locationEdit.getText().toString() != null) {
                if (locationEdit.getText().toString().trim().equalsIgnoreCase("near me")) {
                    double lat = mLastLocation.getLatitude();
                    double lon = mLastLocation.getLongitude();
                    new ProgressTask(MainActivity.this).execute(lat+","+lon, keyword, radius);
                } else {
                    new ProgressTask(MainActivity.this).execute(locationEdit.getText().toString(), keyword, radius);
                }
            } else {
                Log.i("MainActivity","Location is null, so will resort to default location");
                new ProgressTask(MainActivity.this).execute("Shadyside Pittsburgh PA", keyword, radius);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private class ProgressTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog dialog;

        public ProgressTask(ActionBarActivity activity) {
            Log.i("1", "Calling");
            context = activity;
            dialog = new ProgressDialog(context);
        }

        public ProgressTask(ListActivity activity) {

            Log.i("1", "Called");
            context = activity;
            dialog = new ProgressDialog(context);
        }

        private Context context;

        protected void onPreExecute() {
            this.dialog.setMessage("Progress start");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }


            //ListAdapter adapter = new SimpleAdapter(context, jsonlist, R.layout.list_activity, new String[] { type, color, fuel, rating }, new int[] { R.id.vehicleType, R.id.vehicleColor, R.id.fuel, R.id.ratingBar });

            RatingAdapter adapter = new RatingAdapter(jsonlist);
            ratingAdapter = adapter;

            ListView myList=(ListView)findViewById(android.R.id.list);

            myList.setAdapter(null);

            myList.setAdapter(adapter);

            //setListAdapter(adapter);
            //lv = getListView();

        }

        protected Boolean doInBackground(final String... args) {

            VcJsonReader jParser = new VcJsonReader();

            //String download_url = args[0];
            String location = args[0];
            String keyword = args[1];
            String radius = args[2];

            //Log.v("MainActivity","Download URL:\n"+download_url);

            //JSONArray json = jParser.getJSONFromUrl(url);
            String response = jParser.getJSONFromUrl(location,keyword,radius);

            try {

                JSONTokener tokener = new JSONTokener(response);
                Log.v("MainActivity", "----- Tokens from JSON Parsing -------");
                //while (tokener.more()) {
                JSONObject responseObject = (JSONObject) tokener.nextValue();

                //JSONTokener sub_tokener = new JSONTokener(responseObject.getJSONObject("table"));

                JSONObject table = responseObject.getJSONObject("table");


                /*
                while (sub_tokener.more()) {
                    JSONObject sub_response = (JSONObject) sub_tokener.nextValue();
                    Log.v("MainActivity",sub_response.toString());
                }*/

                  //  Log.v("MainActivity",responseObject.toString());
                //}

                //response = (JSONObject) new JSONTokener(resp).nextValue();

                JSONArray restaurants = table.getJSONObject("restaurants").getJSONArray("results");
                Log.v("MainActivity","Downloaded Restaurants:\n"+restaurants);

                JSONArray dishes = table.getJSONObject("dishes").getJSONArray("results");

                Log.v("MainActivity","Downloaded Dishes:\n"+dishes);
                jsonlist.clear();

                for (int i = 0; i < dishes.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();

                    JSONObject dish = dishes.getJSONObject(i);

                    map.put(type, dish.getString("name"));

                    String restaurantName = dish.getJSONObject("restaurant").getString("name");

                    String distance = dish.getString("distance_from_search");
                    Log.v("MainActivity","distance_from_search: "+distance);
                    if ((distance != null) && !distance.equals("")) {
                        restaurantName += distance;
                    }

                    map.put(color, restaurantName);

                    map.put(description,dish.getString("description"));

                    double avg_rating = dish.getDouble("avg_rating");
                    map.put(rating,""+avg_rating);

                    String price =dish.getString("price_in_dollars");

                    if ((price == null) || price.equals("null"))
                        map.put(fuel, "");
                    else
                        map.put(fuel,price);


                    jsonlist.add(map);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Log.v("MainActivity","read json:\n"+json.toString());

            /*
            for (int i = 0; i < json.length(); i++) {

                try {
                    JSONObject c = json.getJSONObject(i);
                    String vtype = c.getString(type);

                    String vcolor = c.getString(color);
                    String vfuel = c.getString(fuel);

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(type, vtype);
                    map.put(color, vcolor);
                    map.put(fuel, vfuel);


                    jsonlist.add(map);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            */
            return null;

        }
    }

    private RowModel getModel(int position) {
        //return(((RatingAdapter)getAdapter()).getItem(position));
        return (RowModel)ratingAdapter.getItem(position);
    }

    class RatingAdapter extends ArrayAdapter {
        ArrayList<HashMap<String, String>> jsonlist;
        RatingAdapter(ArrayList list) {
            super(MainActivity.this, R.layout.list_activity, list);
            jsonlist = list;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row=convertView;
            //ViewWrapper wrapper;
            DishViewWrapper wrapper;
            RatingBar rate;

            final HashMap<String, String> cur_dish = (HashMap<String, String>) jsonlist.get(position);

            if (row==null) {
                LayoutInflater inflater=getLayoutInflater();
                row=inflater.inflate(R.layout.list_activity, parent, false);
                wrapper=new DishViewWrapper(row);
                row.setTag(wrapper);
                rate=wrapper.getRatingBar();
                RatingBar.OnRatingBarChangeListener l=
                        new RatingBar.OnRatingBarChangeListener() {
                            public void onRatingChanged(RatingBar ratingBar,
                                                        float rating, boolean fromTouch) {
                                Integer myPosition=(Integer)ratingBar.getTag();
                                //RowModel model=getModel(myPosition);
                                //model.rating=rating;
                                //model.dishName = cur_dish.get(type);
                                //model.restaurant = cur_dish.get(color);
                                LinearLayout parent=(LinearLayout)ratingBar.getParent();
                                TextView vehicleType=(TextView)parent.findViewById(R.id.vehicleType);

                                // Save the rating

                                //vehicleType.setText(model.dishName);
                            }
                        };
                rate.setOnRatingBarChangeListener(l);
            }
            else {
                wrapper=(DishViewWrapper)row.getTag();
                rate=wrapper.getRatingBar();
            }

            //RowModel model=getModel(position);

            wrapper.getVehicleType().setText(cur_dish.get(type));
            wrapper.getVehicleColor().setText(cur_dish.get(color));
            rate.setTag(new Integer(position));
            rate.setRating(Float.parseFloat(cur_dish.get(rating)));
            wrapper.getDescription().setText(cur_dish.get(description));
            return(row);
        }
    }

    class RowModel {
        //String label;
        String dishName;
        String restaurant;
        float rating=2.0f;

        RowModel(String dishName) {
            this.dishName=dishName;
        }

        public String toString() {
            if (rating>=3.0) {
                return(dishName.toUpperCase());
            }
            return(dishName);
        }
    }
}
