package com.vchoose.Vchoose;
/*
* This is the main container for all things
* */

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.util.Pair;
import android.widget.TextView;

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

    private TextView linkTextview;

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

        linkTextview = (TextView) findViewById(R.id.txtLink);
        linkTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });
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

    private void createDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Privacy Policy");
        builder.setMessage("PRIVACY POLICY\n" +
                "This privacy policy governs your use of the software application vChoose (“Application”) for mobile devices that was created by . The Application is search engine for dishes which also provides personalized food recommendations to the users.\n" +
                "What information does the Application obtain and how is it used?\n" +
                "User Provided Information\n" +
                "The Application obtains the information you provide when you download and register the Application. Registration with us is optional. However, please keep in mind that you may not be able to use some of the features offered by the Application unless you register with us.\n" +
                "When you register with us and use the Application, you generally or may provide (a) your name, email address, age, user name, password and other registration information; (b) transaction-related information, such as when you make purchases, respond to any offers, or download or use applications from us; (c) information you provide us when you contact us for help; (d) credit card information for purchase and use of the Application, and; (e) information you enter into our system when using the Application, such as contact information and project management information.\n" +
                "We may also use the information you provided us to contact your from time to time to provide you with important information, required notices and marketing promotions.\n" +
                "Automatically Collected Information\n" +
                "In addition, the Application may collect certain information automatically, including, but not limited to, the type of mobile device you use, your mobile devices unique device ID, the IP address of your mobile device, your mobile operating system, the type of mobile Internet browsers you use, and information about the way you use the Application.\n" +
                "When you visit the mobile application, we use GPS technology (or other similar technology) to determine your current location in order to determine the city you are located within and display a location map with relevant advertisements. We will not share your current location with other users or partners.\n" +
                "If you do not want us to use your location for the purposes set forth above, you should turn off the location services for the mobile application located in your account settings or in your mobile phone settings and/or within the mobile application.\n" +
                "Do third parties see and/or have access to information obtained by the Application?\n" +
                "Yes. We will share your information with third parties only in the ways that are described in this privacy statement.\n" +
                "We may disclose User Provided and Automatically Collected Information:\n" +
                "   \n" +
                "·\tas required by law, such as to comply with a subpoena, or similar legal process;\n" +
                "·\twhen we believe in good faith that disclosure is necessary to protect our rights, protect your safety or the safety of others, investigate fraud, or respond to a government request;\n" +
                "·\twith our trusted services providers who work on our behalf, do not have an independent use of the information we disclose to them, and have agreed to adhere to the rules set forth in this privacy statement.\n" +
                "·\tif is involved in a merger, acquisition, or sale of all or a portion of its assets, you will be notified via email and/or a prominent notice on our Web site of any change in ownership or uses of this information, as well as any choices you may have regarding this information.\n" +
                "What are my opt-out rights?\n" +
                "You can stop all collection of information by the Application easily by uninstalling the Application. You may use the standard uninstall processes as may be available as part of your mobile device or via the mobile application marketplace or network. You can also request to opt-out via email.\n" +
                "Data Retention Policy, Managing Your Information\n" +
                "We will retain User Provided data for as long as you use the Application and for a reasonable time thereafter. We will retain Automatically Collected information for up to 24 months and thereafter may store it in aggregate. If you’d like us to delete User Provided Data that you have provided via the Application, please contact us at and we will respond in a reasonable time. Please note that some or all of the User Provided Data may be required in order for the Application to function properly.\n" +
                "Security\n" +
                "We are concerned about safeguarding the confidentiality of your information. We provide physical, electronic, and procedural safeguards to protect information we process and maintain. For example, we limit access to this information to authorized employees and contractors who need to know that information in order to operate, develop or improve our Application. Please be aware that, although we endeavor provide reasonable security for information we process and maintain, no security system can prevent all potential security breaches.\n" +
                "Changes\n" +
                "This Privacy Policy may be updated from time to time for any reason. We will notify you of any changes to our Privacy Policy by posting the new Privacy Policy here and informing you via email or text message. You are advised to consult this Privacy Policy regularly for any changes, as continued use is deemed approval of all changes.\n" +
                "Your Consent\n" +
                "By using the Application, you are consenting to our processing of your information as set forth in this Privacy Policy now and as amended by us. \"Processing,” means using cookies on a computer/hand held device or using or touching information in any way, including, but not limited to, collecting, storing, deleting, using, combining and disclosing information, all of which activities will take place in the United States. If you reside outside the United States your information will be transferred, processed and stored there under United States privacy standards.\n" +
                "Contact us\n" +
                "If you have any questions regarding privacy while using the Application, or have questions about our practices, please contact us via email at support@vchoose.us.\n");
        builder.create().show();
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
                    map.put("restaurant_name", dish.getJSONObject("restaurant").getString("name"));
                    map.put("restaurant_phone", dish.getJSONObject("restaurant").getString("phone"));
                    map.put("restaurant_location", dish.getJSONObject("restaurant").getJSONObject("location").getString("full_address"));

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