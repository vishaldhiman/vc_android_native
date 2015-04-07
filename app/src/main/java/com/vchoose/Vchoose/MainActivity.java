package com.vchoose.Vchoose;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.vchoose.Vchoose.com.vchoose.Vchoose.api.calls.SubmitRatings;
import com.vchoose.Vchoose.util.VcJsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    private Context context;
    private static String url = "http://docs.blackberry.com/sampledata.json";

    private static final String dishname = "vehicleType";
    private static final String location = "vehicleColor";
    private static final String fuel = "fuel";
    private static final String rating = "rating";
    private static final String description = "description";

    public static String AuthenticationToken;

    AutoCompleteTextView mEdit;
    EditText locationEdit;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    Spinner spinner;
    RatingBar ratingBar;

    RatingAdapter ratingAdapter;

    ArrayList<String> hint = new ArrayList<String>();
    ArrayList<HashMap<String, String>> jsonlist = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_gridlayout);
        mEdit   = (AutoCompleteTextView)findViewById(R.id.editText);
        context = this;
        mEdit.addTextChangedListener(new InputValidator());
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
        mGoogleApiClient.connect();
        Log.v("GoogleApiClient",String.valueOf(mGoogleApiClient.isConnected()));

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
        Log.v("MainActivity","onConnected - Found LastLocation.\n"+mLastLocation);
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

    public void checkDish(View view) {
        Intent intent = new Intent(this, DishInfo.class);
        TextView tv = (TextView)view.findViewById(R.id.Dish_name);
        TextView tv2 = (TextView)view.findViewById(R.id.description);
        //DishInfo.DIS=tv2.getText().toString();
        //DishInfo.NAME=tv.getText().toString();
        ArrayList<String> stringList = new ArrayList<String>();
        stringList.add(tv.getText().toString());
        stringList.add(tv2.getText().toString());
        //intent.putExtra(DishInfo.DIS, tv2.getText());
        intent.putExtra("DishInfo", stringList);
        //intent.putStringArrayListExtra("ListString", stringList);
        startActivity(intent);
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

        mGoogleApiClient.connect();
        Log.v("GoogleApiClient",String.valueOf(mGoogleApiClient.isConnected()));

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        Log.v("MainActivity","Found LastLocation.\n"+mLastLocation);

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

        if(id == R.id.login) {
            Intent intent = new Intent(this,Login.class);
            startActivityForResult(intent, 1);
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
            //ListAdapter adapter = new SimpleAdapter(context, jsonlist, R.layout.list_activity, new String[] { dishname, location, fuel, rating }, new int[] { R.id.vehicleType, R.id.vehicleColor, R.id.fuel, R.id.ratingBar });

            RatingAdapter adapter = new RatingAdapter(jsonlist);
            ratingAdapter = adapter;

            ListView myList=(ListView)findViewById(android.R.id.list);

            myList.setAdapter(adapter);

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.v("clicked","true");
                    Intent intent = new Intent(context, DishInfo.class);
                    HashMap<String, String> dishes = (HashMap<String, String>) jsonlist.get(position);
                    String name_text = dishes.get(dishname);
                    String description_text = dishes.get(description);
                    String location_text = dishes.get(location);
                    ArrayList<String> stringList = new ArrayList<String>();
                    stringList.add(name_text);
                    stringList.add(description_text);
                    stringList.add(location_text);
                    intent.putExtra("DishInfo", stringList);
                    startActivity(intent);
                }

            });
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

                JSONObject table = responseObject.getJSONObject("dishes");


                /*
                while (sub_tokener.more()) {
                    JSONObject sub_response = (JSONObject) sub_tokener.nextValue();
                    Log.v("MainActivity",sub_response.toString());
                }*/

                  //  Log.v("MainActivity",responseObject.toString());
                //}

                //response = (JSONObject) new JSONTokener(resp).nextValue();

                //get the result of restaurants
                /*
                JSONArray restaurants = table.getJSONObject("restaurants").getJSONArray("results");
                Log.v("MainActivity","Downloaded Restaurants:\n"+restaurants);
                */
                JSONArray dishes = table.getJSONArray("results");

                Log.v("MainActivity","Downloaded Dishes:\n"+dishes);
                jsonlist.clear();

                for (int i = 0; i < dishes.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();

                    JSONObject dish = dishes.getJSONObject(i);

                    map.put(dishname, dish.getString("name"));

                    String restaurantName = dish.getJSONObject("restaurant").getString("name");

                    String distance = dish.getJSONObject("restaurant").getJSONObject("distance").getString("string");
                    //Log.v("MainActivity","distance: "+distance);
                    if ((distance != null) && !distance.equals("")) {
                        restaurantName += distance;
                    }

                    map.put(MainActivity.location, restaurantName);

                    map.put(description,dish.getString("description"));

                    map.put("ID", dish.getString("id"));


                    double avg_rating = dish.getJSONObject("rating").getDouble("avg");
                    map.put(rating,""+avg_rating);

                    ;
                    String price =dish.getJSONObject("price").getString("dollars");

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
                    String vtype = c.getString(dishname);

                    String vcolor = c.getString(location);
                    String vfuel = c.getString(fuel);

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(dishname, vtype);
                    map.put(location, vcolor);
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

        public View getView(final int position, View convertView, ViewGroup parent) {

            View row=convertView;
            //ViewWrapper wrapper;
            DishViewWrapper wrapper;
            RatingBar rate;

            final HashMap<String, String> cur_dish = (HashMap<String, String>) jsonlist.get(position);

            if (row==null) {
                LayoutInflater inflater=getLayoutInflater();
                row=inflater.inflate(R.layout.list_activity, parent, false);//set the list view
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
                                  new SubmitRatings(MainActivity.this).execute(cur_dish.get("ID"),String.valueOf(rating));

                                  //the Rating is stored here
                              }
                          }
                      }
                    );
            }
            else {
                wrapper=(DishViewWrapper)row.getTag();
                rate=wrapper.getRatingBar();
            }

            //RowModel model=getModel(position);

            wrapper.getVehicleType().setText(cur_dish.get(dishname));
            wrapper.getVehicleColor().setText(cur_dish.get(location));
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
    private class InputValidator implements TextWatcher {

        public void afterTextChanged(Editable s) {
            Log.v("The text is changed","changed");
            final String keyword_new = mEdit.getText().toString();

            /* start an thread for auto complete */
            Thread t = new Thread(new Runnable() {
                String keyword_new_inner = keyword_new;
                public void run() {
                    VcJsonReader reader = new VcJsonReader();
                    hint = reader.getAutoComplete(keyword_new_inner);
                    }
            });

            t.start();
            try {
                t.join();
            } catch (InterruptedException e){}

            ArrayAdapter adapter = new ArrayAdapter
                    (context,android.R.layout.simple_list_item_1, hint);
            mEdit.setAdapter(adapter);
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            this.AuthenticationToken = data.getStringExtra("AuthenticationToken");
            Log.v("AuthenticationToken",this.AuthenticationToken);
        }
    }
}
