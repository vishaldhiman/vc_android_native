package com.vchoose.Vchoose;
/*
* This is the main container for all things
* */

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.util.Pair;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.vchoose.Vchoose.util.VcJsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class MainPagerActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private static final String dishname = "vehicleType";
    private static final String location = "vehicleColor";
    private static final String fuel = "fuel";
    private static final String rating = "rating";
    private static final String description = "description";

    private static final String restaurantName = "name";
    private static final String restaurantLocation = "location";
    private static final String restaurantDistance = "distance";
    private static final String restaurantRating = "rating";
    private static final String restaurantDescription = "description";
    private static final String restaurantPhone = "phone";
    private static final String getRestaurantRatingImageUrl = "ratingImage";

    public static String AuthenticationToken;// = "hG4T5oT96uwzDYbxpnST";//hard coded for testing

    SectionsPagerAdapter mSectionsPagerAdapter;

    private Context context;
    AutoCompleteTextView mEdit;
    EditText locationEdit;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    Spinner spinner;
    Button searchButton;
    ArrayList<String> hint = new ArrayList<String>();
    ArrayList<HashMap<String, String>> dishJsonlist = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> restaurantJsonlist = new ArrayList<HashMap<String, String>>();
    ArrayList<Pair<String, LatLng>> mapMarkers = new ArrayList<Pair<String, LatLng>>();


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        mEdit   = (AutoCompleteTextView)findViewById(R.id.editText);
        locationEdit = (EditText)findViewById(R.id.editTextLocation);
        spinner = (Spinner) findViewById(R.id.spinner);
        searchButton = (Button)findViewById(R.id.searchButton);
        context = this;

        mEdit.addTextChangedListener(new InputValidator());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.radius_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });
        buildGoogleApiClient();
        mGoogleApiClient.connect();


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        // Set up the ViewPager with the sections adapter.
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
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

    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.v("MainActivity","onConnected - Found LastLocation.\n"+mLastLocation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        if(id == R.id.login) {
            Intent intent = new Intent(this,Login.class);
            startActivityForResult(intent, 1);
        }

        return super.onOptionsItemSelected(item);
    }

    public void doSearch() {
        String keyword = mEdit.getText().toString();
        Log.i("MyActivity","inside doSearch");
        Log.v("MyActivity","inside doSearch. keyword: "+keyword);
        //this.me

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
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
            dishJsonlist.clear();

            String radiusString = spinner.getSelectedItem().toString();

            String radius = radiusString.split(" ")[0];     //from '0.5 mi' extract 0.5

            if (locationEdit.getText().toString() != null) {
                if (locationEdit.getText().toString().trim().equalsIgnoreCase("near me")) {
                    double lat = mLastLocation.getLatitude();
                    double lon = mLastLocation.getLongitude();
                    new ProgressTask(this).execute(lat + "," + lon, keyword, radius);
                } else {
                    new ProgressTask(this).execute(locationEdit.getText().toString(), keyword, radius);
                }
            } else {
                Log.i("MainActivity","Location is null, so will resort to default location");
                new ProgressTask(this).execute("Shadyside Pittsburgh PA", keyword, radius);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ProgressTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog dialog;

        public ProgressTask(Activity activity) {
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

            if(mSectionsPagerAdapter == null) {
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), dishJsonlist, restaurantJsonlist, mapMarkers);
                Log.v("restaurantJsonlist",String.valueOf(restaurantJsonlist.size()));
                mViewPager.setAdapter(mSectionsPagerAdapter);
                mViewPager.setCurrentItem(1);
            } else {
                mSectionsPagerAdapter.notifyDataSetChanged();
            }

            //ListAdapter adapter = new SimpleAdapter(context, dishJsonlist, R.layout.dish_list_componet, new String[] { dishname, location, fuel, rating }, new int[] { R.id.vehicleType, R.id.vehicleColor, R.id.fuel, R.id.ratingBar });
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

                //JSONTokener sub_tokener = new JSONTokener(responseObject.getJSONObject("dishTable"));

                JSONObject dishTable = responseObject.getJSONObject("dishes");
                JSONObject restaurantTable = responseObject.getJSONObject("restaurants");

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
                JSONArray restaurants = dishTable.getJSONObject("restaurants").getJSONArray("results");
                Log.v("MainActivity","Downloaded Restaurants:\n"+restaurants);
                */
                JSONArray dishes = dishTable.getJSONArray("results");
                JSONArray restaurants = restaurantTable.getJSONArray("results");

                Log.v("MainActivity","Downloaded Dishes:\n"+dishes);
                dishJsonlist.clear();
                restaurantJsonlist.clear();

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

                    JSONArray tags = dish.getJSONArray("tags");
                    //Log.v("mydish","mydish");
                    //Log.v(dish.getString("name"), String.valueOf(tags.length()));
                    String s[] = new String[3];

                    for(int j = 0; ( j < tags.length() )&&( j < 3 ); j++) {
                        s[j] = tags.getJSONObject(tags.length()-j-1).getString("name");
                        Log.v("My Tags" + j, s[j]);
                        map.put("Tag"+j,s[j]);
                    }

                    map.put(MainPagerActivity.location, restaurantName);

                    map.put(description,dish.getString("description"));

                    map.put("ID", dish.getString("id"));
                    map.put("restaurant_id", dish.getJSONObject("restaurant").getString("id"));

                    map.put("thumbnail",dish.getString("thumbnail"));


                    double avg_rating = dish.getJSONObject("rating").getDouble("avg");
                    map.put(rating,""+avg_rating);

                    String price =dish.getJSONObject("price").getString("dollars");

                    if ((price == null) || price.equals("null"))
                        map.put(fuel, "");
                    else
                        map.put(fuel,price);

                    dishJsonlist.add(map);
                }

                Log.v("restaurants.length()", String.valueOf(restaurants.length()));

                for (int i = 0; i < restaurants.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();

                    JSONObject restaurant = restaurants.getJSONObject(i);

                    map.put(restaurantName, restaurant.getString("name"));
                    map.put(restaurantPhone, restaurant.getString("phone"));
                    map.put(restaurantLocation, restaurant.getJSONObject("location").getString("full_address"));

                    String distance = restaurant.getJSONObject("distance").getString("string");
                    map.put(restaurantDistance, distance);

                    String avg_rating = restaurant.getJSONObject("rating").getJSONObject("yelp").getString("avg");
                    map.put(restaurantRating,avg_rating);


                    if(!restaurant.getJSONObject("rating").getJSONObject("yelp").get("meta").toString().equals("null")){
                        Log.v("meta",restaurant.getJSONObject("rating").getJSONObject("yelp").get("meta").toString());
                        JSONObject meta = restaurant.getJSONObject("rating").getJSONObject("yelp").getJSONObject("meta");
                        String url = meta.getString("rating_img_url");
                        String yelp_mobile_url = meta.getString("mobile_url");
                        map.put(getRestaurantRatingImageUrl, url);
                        map.put("yelp_mobile_url",yelp_mobile_url);
                    }

                    JSONArray tags = restaurant.getJSONArray("tags");
                    String s[] = new String[3];

                    for(int j = 0; ( j < tags.length() )&&( j < 3 ); j++) {
                        s[j] = tags.getJSONObject(tags.length()-j-1).getString("name");
                        Log.v("My Tags" + j, s[j]);
                        map.put("Tag"+j,s[j]);
                    }

                    restaurantJsonlist.add(map);
                }

                if (mapMarkers != null)
                    mapMarkers.clear();

                JSONArray jsonMapMarkers = responseObject.getJSONArray("map_markers");
                for (int i = 0; i < jsonMapMarkers.length(); i++) {
                    JSONObject jsonMapMarker = jsonMapMarkers.getJSONObject(i);

                    Pair<String, LatLng> pair = null;
                    try {
                        LatLng latLng = new LatLng(jsonMapMarker.getDouble("lat"),jsonMapMarker.getDouble("lng"));
                        String restaurantName = jsonMapMarker.getString("infowindow");

                        if (restaurantName != null) {
                            restaurantName = restaurantName.replaceAll("<h3>", "");
                            restaurantName = restaurantName.replaceAll("</h3>", "");
                            restaurantName = restaurantName.trim();
                        }

                        pair = new Pair<>(restaurantName,latLng);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mapMarkers.add(pair);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        ArrayList<HashMap<String, String>> adapterDishJsonlist;
        ArrayList<HashMap<String, String>> adapterRestaurantJsonlist;
        ArrayList<Pair<String, LatLng>> mapMarkers;

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<HashMap<String, String>> jsonlist, ArrayList<HashMap<String, String>> jsonlist2, ArrayList<Pair<String, LatLng>> mMarkers) {
            super(fm);
            adapterDishJsonlist = jsonlist;
            adapterRestaurantJsonlist = jsonlist2;
            mapMarkers = mMarkers;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 1) {
                DishesListFragment tf = new DishesListFragment();        //choose what to display for which tag
                tf.setArrayList(adapterDishJsonlist);
                tf.setAuthenticationToken(AuthenticationToken);
                return tf;
            } else if(position == 0) {
                RestaurantListFragment tf = new RestaurantListFragment();
                tf.setArrayList(adapterRestaurantJsonlist);
                return tf;
            } else if (position == 2) {
                MapsFragment mapsFragment = new MapsFragment();
                mapsFragment.setMapMarkers(mapMarkers);
                return mapsFragment;
            } else {
                return PlaceholderFragment.newInstance(position + 1);
            }
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Restaurants("+adapterRestaurantJsonlist.size()+")";//.toUpperCase(l);
                case 1:
                    return "Dishes("+adapterDishJsonlist.size()+")";//.toUpperCase(l);
                case 2:
                    return "Map("+mapMarkers.size()+")";//.toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_pager, container, false);
            return rootView;
        }
    }

    private class InputValidator implements TextWatcher {

        public void afterTextChanged(Editable s) {
            Log.v("The text is changed", "changed");
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
            Log.v("AuthenticationToken", this.AuthenticationToken);
        }
    }
}