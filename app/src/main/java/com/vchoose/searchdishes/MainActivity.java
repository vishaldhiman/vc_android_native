package com.vchoose.searchdishes;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.vchoose.searchdishes.util.VcJsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {
    private String url_prefix = "http://vchoose.us/api/v1/public/search.json?";
    private String loc = "search_locations=";
    private String keyword = "search_tags=";
    private String radius = "search_radius=";

    private Context context;
    private static String url = "http://docs.blackberry.com/sampledata.json";

    private static final String type = "vehicleType";
    private static final String color = "vehicleColor";
    private static final String fuel = "fuel";

    EditText mEdit;

    ArrayList<HashMap<String, String>> jsonlist = new ArrayList<HashMap<String, String>>();

    ListView lv ;

    public String buildUrl(String location, String search_keyword, String rad) {
        return url_prefix+loc+location+"&"+keyword+search_keyword+"&"+radius+rad;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_gridlayout);
        mEdit   = (EditText)findViewById(R.id.editText);
        Log.i("MyActivity","inside onCreate");
        //new ProgressTask(MainActivity.this).execute();
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

            //clear the data before downloading again
        jsonlist.clear();
        new ProgressTask(MainActivity.this).execute();
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


            ListAdapter adapter = new SimpleAdapter(context, jsonlist, R.layout.list_activity, new String[] { type, color, fuel }, new int[] { R.id.vehicleType, R.id.vehicleColor, R.id.fuel });
            ListView myList=(ListView)findViewById(android.R.id.list);

            //myList.setAdapter(null);

            myList.setAdapter(adapter);

            //setListAdapter(adapter);
            //lv = getListView();

        }

        protected Boolean doInBackground(final String... args) {

            VcJsonReader jParser = new VcJsonReader();
            JSONArray json = jParser.getJSONFromUrl(url);

            Log.v("MainActivity","read json:\n"+json.toString());

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
            return null;

        }
    }
}
