package com.vchoose.Vchoose;
/*
* This is the main container for all things
* */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.gc.materialdesign.views.ButtonRectangle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.vchoose.Vchoose.util.InternalSorage;
import com.vchoose.Vchoose.util.MyCustomProgressDialog;
import com.vchoose.Vchoose.util.PlaceAutocompleteAdapter;
import com.vchoose.Vchoose.util.User;
import com.vchoose.Vchoose.util.VcJsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainPagerActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private static final String TAG = "SamT_";

    /* keywords for dish */
    private static final String dishname = "dishName";
    private static final String dishID = "ID";
    private static final String location = "location";
    private static final String dishUrl = "url";
    private static final String price = "price";
    private static final String rating = "rating";
    private static final String description = "description";
    private static final String dishTag = "Tag";
    private static final String provider = "provider";
    private static final String provider_name = "provider_name";
    private static final String thumbnail = "thumbnail";
    private static final String review = "review";
    private static final String reviewer = "reviewer";
    private static final String reviewerImage = "image";
    private static final String reviewRating = "rating";

    /* keywords for restaurant information from dish */
    private static final String dishRestID = "restaurant_id";
    private static final String dishRestName = "restaurant_name";
    private static final String dishRestPhone = "restaurant_phone";
    private static final String dishRestLocation = "restaurant_location";
    private static final String dishRestLatitude = "latitude";
    private static final String dishRestLongitude = "longitude";

    /* keywords for restaurant info */
    private static final String restaurantID = "id";
    private static final String restaurantName = "name";
    private static final String restaurantLocation = "location";
    private static final String restaurantDistance = "distance";
    private static final String restaurantRating = "rating";
    //private static final String restaurantDescription = "description";
    private static final String restaurantPhone = "phone";
    private static final String restaurantTag = "Tag";
    private static final String getRestaurantRatingImageUrl = "ratingImage";
    private static final String yelpLink = "yelp_mobile_url";

    SectionsPagerAdapter mSectionsPagerAdapter;
    private Context context;
    private Menu menu;

    AutoCompleteTextView keyWord;
    AutoCompleteTextView locationEdit;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    Spinner spinner;
    ButtonRectangle searchButton;
    Button virtualSearchButton;
    ImageButton myLocation;
    ViewPager mViewPager;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ImageView userPhoto;
    TextView userName;
    WelcomePagerAdapter welcomePagerAdapter;

    ArrayList<String> hint = new ArrayList<>();
    ArrayList<HashMap<String, String>> dishJsonlist = new ArrayList<>();
    ArrayList<ArrayList<HashMap<String, String>>> dishReviewJsonlist = new ArrayList<>();
    ArrayList<HashMap<String, String>> restaurantJsonlist = new ArrayList<>();
    ArrayList<Pair<String, LatLng>> mapMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);


        /* tool bar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
/*
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
*/
        getSupportActionBar().hide();
        setSupportActionBar(toolbar);
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setTintColor(getResources().getColor(R.color.greenDark));

        mViewPager = (ViewPager) findViewById(R.id.pager);


        /* drawer */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        String[] drawer_array_unlogin = getResources().getStringArray(R.array.drawer_array_unlogin);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, drawer_array_unlogin));
        View mHeader = getLayoutInflater().inflate(R.layout.navigation_list_header, mDrawerList, false);
        userName = (TextView) mHeader.findViewById(R.id.userName);
        TextView userEmail = (TextView) mHeader.findViewById(R.id.userEmail);
        userPhoto = (ImageView) mHeader.findViewById(R.id.userPhoto);
        ImageView userBackground = (ImageView) mHeader.findViewById(R.id.userBackground);
        mDrawerList.addHeaderView(mHeader);
        userPhoto.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.abc_btn_radio_material,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        keyWord = (AutoCompleteTextView)findViewById(R.id.keyword);
        locationEdit = (AutoCompleteTextView)findViewById(R.id.editTextLocation);
        spinner = (Spinner) findViewById(R.id.spinner);
        searchButton = (ButtonRectangle)findViewById(R.id.searchButton);
        myLocation = (ImageButton) findViewById(R.id.myLocation);

        context = this;
        
        /* auto complete */
        keyWord.addTextChangedListener(new InputValidator());
        
        /*
        linkTextview = (TextView) findViewById(R.id.txtLink);
        linkTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });
        */

        //mEdit.addTextChangedListener(new InputValidator());

        /* distance spinner */
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.radius_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        /* search button */
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });
        virtualSearchButton = new Button(context);
        virtualSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });

        /* Near Me button */
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationEdit.setText("Near Me");
                locationEdit.dismissDropDown();
            }
        });

        /* keyboard search button */
        keyWord.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.v(TAG + "actionID", String.valueOf(actionId));
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    doSearch();
                    return true;
                }
                return false;
            }
        });
        locationEdit.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.v(TAG + "actionID", String.valueOf(actionId));
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    doSearch();
                    return true;
                }
                return false;
            }
        } );

        /* Google location api */
        buildGoogleApiClient();
        mGoogleApiClient.connect();

         /* Location autocomplete */
        PlaceAutocompleteAdapter mAdapter;
        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, null);
        locationEdit.setAdapter(mAdapter);

        /* Welcome hints */
        welcomePagerAdapter = new WelcomePagerAdapter(getSupportFragmentManager(), searchButton, keyWord, virtualSearchButton);
        mViewPager.setAdapter(welcomePagerAdapter);
        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);

        //for testing


        keyWord.setText("pizza");
        /*Button testButton = (Button)findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), RestaurantInfo.class);

                intent.putExtra(dishRestID, "9280");//9280 with pic; 17000
                intent.putExtra(dishRestName, "Test");
                intent.putExtra(dishRestPhone, "Test");
                intent.putExtra(dishRestLocation, "Test");
                startActivity(intent);
            }
        });
        */
        //doSearch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pager, menu);
        this.menu = menu;
        return true;
    }

    /* action bar menu */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.login) {
            Intent intent = new Intent(this, Login.class);
            startActivityForResult(intent, 1);
        }

        return super.onOptionsItemSelected(item);
    }

    public void doSearch() {
        String keyword = keyWord.getText().toString();
        Log.v(TAG + "MainPagerActivity", "inside doSearch. keyword: " + keyword);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mGoogleApiClient.connect();
        Log.v(TAG + "MainPagerActivity", "mGoogleApiClient:" + String.valueOf(mGoogleApiClient.isConnected()));

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        Log.v(TAG + "MainPagerActivity","Found LastLocation.\n"+mLastLocation);

        /* hide the keyboard */
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(keyWord.getWindowToken(), 0);

        try {
            //clear the data before downloading again
            dishJsonlist.clear();

            String radiusString = spinner.getSelectedItem().toString();

            String radius = radiusString.split(" ")[0];     //from '0.5 mi' extract 0.5

            if (locationEdit.getText().toString() != null) {
                if (locationEdit.getText().toString().trim().equalsIgnoreCase("near me")) {
                    double lat = mLastLocation.getLatitude();
                    double lon = mLastLocation.getLongitude();
                    Log.v(TAG + "Location Now", String.valueOf(lat)+ "," + String.valueOf(lon));
                    new ProgressTask(this).execute(lat + "," + lon, keyword, radius);
                } else {
                    new ProgressTask(this).execute(locationEdit.getText().toString(), keyword, radius);
                }
            } else {
                Log.i(TAG + "MainPagerActivity","Location is null, so will resort to default location");
                new ProgressTask(this).execute("Shadyside Pittsburgh PA", keyword, radius);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ProgressTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog dialog;
        private Context context;

        public ProgressTask(Activity activity) {
            Log.i(TAG + "MainPagerActivity", "ProgressTask Calling");
            context = activity;
            dialog = MyCustomProgressDialog.ctor(context);
        }

        protected void onPreExecute() {
            //this.dialog.setMessage("Searching");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), dishJsonlist, dishReviewJsonlist, restaurantJsonlist, mapMarkers));
            // Bind the tabs to the ViewPager
            PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            tabs.setViewPager(mViewPager);
            mViewPager.setCurrentItem(1);

            /*
            if(mSectionsPagerAdapter == null) {
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), dishJsonlist, dishReviewJsonlist, restaurantJsonlist, mapMarkers);
                Log.v(TAG + "MainPagerActivity","restaurantJsonlist"+String.valueOf(restaurantJsonlist.size()));
                mViewPager.setAdapter(mSectionsPagerAdapter);
                mViewPager.setCurrentItem(1);
            } else {
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
            */
        }

        protected Boolean doInBackground(final String... args) {

            VcJsonReader jParser = new VcJsonReader();

            String location = args[0];
            String keyword = args[1];
            String radius = args[2];

            String response = jParser.getJSONFromUrl(location,keyword,radius);

            try {
                JSONTokener tokener = new JSONTokener(response);
                Log.v(TAG + "MainPagerActivity", "----- Tokens from JSON Parsing -------");
                JSONObject responseObject = (JSONObject) tokener.nextValue();

                JSONObject dishTable = responseObject.getJSONObject("dishes");
                JSONObject restaurantTable = responseObject.getJSONObject("restaurants");
                JSONArray dishes = dishTable.getJSONArray("results");
                JSONArray restaurants = restaurantTable.getJSONArray("results");

                Log.v("MainActivityTest","Downloaded Dishes:\n"+dishes);

                dishJsonlist.clear();
                dishReviewJsonlist.clear();
                restaurantJsonlist.clear();

                for (int i = 0; i < dishes.length(); i++) {
                    HashMap<String, String> map = new HashMap<>();

                    JSONObject dish = dishes.getJSONObject(i);

                    //dish name
                    map.put(dishname, dish.getString("name"));
                    //restaurant name
                    String restaurantName = dish.getJSONObject("restaurant").getString("name");
                    //distance
                    String distance = dish.getJSONObject("restaurant").getJSONObject("distance").getString("string");
                    //Log.v("MainActivity","distance: "+distance);
                    if ((distance != null) && !distance.equals("")) {
                        restaurantName += distance;//combine the restaurant name and distance
                    }
                    //dish location
                    map.put(MainPagerActivity.location, restaurantName);
                    //tags
                    JSONArray tags = dish.getJSONArray("tags");
                    String s[] = new String[3];
                    for(int j = 0; ( j < tags.length() )&&( j < 3 ); j++) {
                        s[j] = tags.getJSONObject(tags.length()-j-1).getString("name");
                        Log.v("My Tags" + j, s[j]);
                        map.put(dishTag + j, s[j]);
                    }

                    //description
                    map.put(description, dish.getString("description"));

                    //customized?
                    String provider = dish.getString("provider");
                    map.put(MainPagerActivity.provider, provider);
                    if(provider.equals("user_added")){
                        map.put(MainPagerActivity.provider_name, dish.getJSONObject("creator").getString("username"));
                    }
                    //dish ID
                    map.put(dishID, dish.getString("id"));
                    //dish URL
                    map.put(dishUrl, dish.getString("url"));
                    //rating
                    double avg_rating = dish.getJSONObject("rating").getDouble("avg");
                    map.put(rating,""+avg_rating);
                    //price
                    String price =dish.getJSONObject("price").getString("dollars");
                    if ((price == null) || price.equals("null"))
                        map.put(MainPagerActivity.price, "");
                    else
                        map.put(MainPagerActivity.price,price);

                    map.put(thumbnail,dish.getString("thumbnail"));
                    //reviews
                    ArrayList<HashMap<String,String>> reviewTemp = new ArrayList<>();
                    for(int j = 0; j < dish.getJSONArray("reviews").length(); j++) {
                        HashMap<String, String> mapReview = new HashMap<>();
                        String review = dish.getJSONArray("reviews").getJSONObject(j).getString("review");
                        String rating = dish.getJSONArray("reviews").getJSONObject(j).getString("rating");
                        String reviewer = dish.getJSONArray("reviews").getJSONObject(j).getJSONObject("reviewer").getString("username");
                        String reviewerImage = dish.getJSONArray("reviews").getJSONObject(j).getJSONObject("reviewer").getString("image");
                        mapReview.put(MainPagerActivity.review,review);
                        mapReview.put(MainPagerActivity.reviewRating,rating);
                        mapReview.put(MainPagerActivity.reviewer,reviewer);
                        mapReview.put(MainPagerActivity.reviewerImage,reviewerImage);
                        Log.v(TAG + "Reviewer", reviewer);
                        reviewTemp.add(mapReview);
                    }
                    dishReviewJsonlist.add(reviewTemp);
                    //restaurant info
                    map.put(dishRestID, dish.getJSONObject("restaurant").getString("id"));
                    map.put(dishRestName, dish.getJSONObject("restaurant").getString("name"));
                    map.put(dishRestPhone, dish.getJSONObject("restaurant").getString("phone"));
                    map.put(dishRestLocation, dish.getJSONObject("restaurant").getJSONObject("location").getString("full_address"));
                    map.put(dishRestLatitude, dish.getJSONObject("restaurant").getJSONObject("location").getString("latitude"));
                    map.put(dishRestLongitude, dish.getJSONObject("restaurant").getJSONObject("location").getString("longitude"));

                    dishJsonlist.add(map);
                }

                Log.v(TAG + "restaurants.length", String.valueOf(restaurants.length()));

                /* restaurant list */
                for (int i = 0; i < restaurants.length(); i++) {
                    HashMap<String, String> map = new HashMap<>();

                    JSONObject restaurant = restaurants.getJSONObject(i);

                    map.put(restaurantID, restaurant.getString("id"));
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
                        map.put(MainPagerActivity.yelpLink, yelp_mobile_url);
                    }

                    JSONArray tags = restaurant.getJSONArray("tags");
                    String s[] = new String[3];

                    for(int j = 0; ( j < tags.length() )&&( j < 3 ); j++) {
                        s[j] = tags.getJSONObject(tags.length()-j-1).getString("name");
                        Log.v("My Tags" + j, s[j]);
                        map.put(MainPagerActivity.restaurantTag + j,s[j]);
                    }

                    restaurantJsonlist.add(map);
                }

                /* map list */
                //if (mapMarkers != null)
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


    // result for login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.v(TAG + "onActivityResult", "get the result back");
            //menu.getItem(0).setIcon(new BitmapDrawable(getResources(), User.getUser_photo()));
            userPhoto.setImageBitmap(User.getUser_photo());
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.v(TAG + "onActivityResult", "Logout");
            //menu.getItem(0).setIcon(android.R.drawable.ic_menu_info_details);
            userPhoto.setImageResource(R.drawable.blank_user);
            userName.setText("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp1=this.getSharedPreferences("Login",0);

        if(sp1 != null) {
            String login_status = sp1.getString("login_status", null);
            String facebookLogin = sp1.getString("facebookLogin", null);
            String Auth_token = sp1.getString("Auth_token", null);
            String Photo_Dir = sp1.getString("Photo_Dir", null);

            if(login_status!=null) {
                if (login_status.equals("true")) {
                    Log.v(TAG+"loginSta",login_status);
                    Log.v(TAG + "Photo_Dir", Photo_Dir);
                    Bitmap b = InternalSorage.get(Photo_Dir);
                    userName.setText(sp1.getString("User_name", null));
                    if (b!=null) {
                        userPhoto.setImageBitmap(b);
                    } else {
                        Log.v(TAG+"Photo_Dir","null");
                    }
                    String[] drawer_array_unlogin = getResources().getStringArray(R.array.drawer_array_login);
                    mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                            R.layout.drawer_list_item, drawer_array_unlogin));
                } else {
                    userPhoto.setImageResource(R.drawable.blank_user);
                    String[] drawer_array_unlogin = getResources().getStringArray(R.array.drawer_array_unlogin);
                    mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                            R.layout.drawer_list_item, drawer_array_unlogin));
                }
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("MainActivityTest","Inside onConnectionSuspended "+i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.v("MainActivityTest","Inside onConnectionFailed "+result.toString());
    }

    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.v("MainActivityTest", "onConnected - Found LastLocation.\n" + mLastLocation);
    }

    /* Pager adapter */
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        ArrayList<HashMap<String, String>> adapterDishJsonlist;
        ArrayList<HashMap<String, String>> adapterRestaurantJsonlist;
        ArrayList<ArrayList<HashMap<String, String>>> adapterReviewDishJsonlist;
        ArrayList<Pair<String, LatLng>> mapMarkers;

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<HashMap<String, String>> jsonlist, ArrayList<ArrayList<HashMap<String, String>>> reviewJsonlist, ArrayList<HashMap<String, String>> jsonlist2, ArrayList<Pair<String, LatLng>> mMarkers) {
            super(fm);
            if (fm.getFragments() != null) {
                fm.getFragments().clear();
            }
            adapterDishJsonlist = jsonlist;
            adapterRestaurantJsonlist = jsonlist2;
            adapterReviewDishJsonlist = reviewJsonlist;
            mapMarkers = mMarkers;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 1) {
                DishesListFragment tf = new DishesListFragment();        //choose what to display for which tag
                tf.setArrayList(adapterDishJsonlist, adapterReviewDishJsonlist);   //pass the data to list
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
            switch (position) {
                case 0:
                    return "Restaurants(" + adapterRestaurantJsonlist.size() + ")";//.toUpperCase(l);
                case 1:
                    return "Dishes(" + adapterDishJsonlist.size() + ")     ";//.toUpperCase(l);
                case 2:
                    return "Map(" + (mapMarkers.size() - 1) + ")       ";//.toUpperCase(l);
            }
            return null;
        }

    }

    public class WelcomePagerAdapter extends FragmentPagerAdapter {
        ButtonRectangle buttonRectangle;
        AutoCompleteTextView textView;
        Button button;

        public WelcomePagerAdapter(FragmentManager fm, ButtonRectangle buttonRectangle, AutoCompleteTextView textView, Button button) {
            super(fm);
            if (fm.getFragments() != null) {
                fm.getFragments().clear();
            }
            this.buttonRectangle = buttonRectangle;
            this.textView = textView;
            this.button = button;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Fragment getItem(int position) {
            WelcomeFragment welcomeFragment = new WelcomeFragment();
            welcomeFragment.setButtonRectangle(buttonRectangle);
            welcomeFragment.setTextView(textView);
            welcomeFragment.setButton(button);
            return welcomeFragment;
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
            return inflater.inflate(R.layout.activity_pager, container, false);
        }
    }

    /* For auto completion */
    private class InputValidator implements TextWatcher {
        private String keyword_new;
        private Timer timer=new Timer();
        private final long DELAY = 500; // in ms
        VcJsonReader reader = new VcJsonReader();

        public void afterTextChanged(Editable s) {
            Log.v("The text is changed", "changed");
            keyword_new = keyWord.getText().toString();

            timer.cancel();
            timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.v(TAG + "keyword_new", keyword_new);
                    hint = reader.getAutoComplete(keyword_new);
                    runOnUiThread(new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter adapter = new ArrayAdapter
                                            (context, android.R.layout.simple_list_item_1, hint);
                                    keyWord.setAdapter(adapter);
                                    Log.v(TAG + "Runnable", "set the autocomplete");
                                }
                            }
                    ));
                }

            }, DELAY);
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }
    }

    /* For location auto completion */
    private class LocationInputValidator implements TextWatcher {
        private String keyword_new;
        private Timer timer=new Timer();
        private final long DELAY = 500; // in ms
        VcJsonReader reader = new VcJsonReader();

        public void afterTextChanged(Editable s) {
            Log.v("The text is changed", "changed");
            keyword_new = keyWord.getText().toString();

            timer.cancel();
            timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.v(TAG + "keyword_new", keyword_new);
                    hint = reader.getAutoComplete(keyword_new);
                    runOnUiThread(new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter adapter = new ArrayAdapter
                                            (context,android.R.layout.simple_list_item_1, hint);
                                    keyWord.setAdapter(adapter);
                                    Log.v(TAG + "Runnable", "set the autocomplete");
                                }
                            }
                    ));
                }

            }, DELAY);
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }
    }
    /* Drawer listener */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent;
            switch (position){
                case 1:
                    intent = new Intent(getApplicationContext(), Login.class);
                    startActivityForResult(intent, 1);
                    break;
                case 2:
                    intent = new Intent(getApplicationContext(), Register.class);
                    startActivity(intent);
                    break;
                case 3:
                    welcomePagerAdapter = new WelcomePagerAdapter(getSupportFragmentManager(), searchButton, keyWord, virtualSearchButton);
                    mViewPager.setAdapter(welcomePagerAdapter);
                    // Bind the tabs to the ViewPager
                    PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
                    tabs.setViewPager(mViewPager);
                    break;
            }
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
}