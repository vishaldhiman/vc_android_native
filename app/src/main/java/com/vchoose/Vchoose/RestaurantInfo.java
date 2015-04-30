package com.vchoose.Vchoose;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.vchoose.Vchoose.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Archie on 04/06/2015.
 */
public class RestaurantInfo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restraunt_info);

        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };


        Bundle extras = getIntent().getExtras();
        //ArrayList<String> stringList = extras.getStringArrayList("RestrauntInfo");
        TextView textview = (TextView)findViewById(R.id.restruant_id);
        TextView textview2=(TextView)findViewById(R.id.location);
        TextView textview3=(TextView)findViewById(R.id.DishPhone);
        ListView list1 = (ListView)findViewById(R.id.list1);

        textview.setText(extras.getString("restaurant_id"));
        //textview.setText(stringList.get(0));
        //textview2.setText(stringList.get(2));
        //textview3.setText(stringList.get(1));

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        list1.setAdapter(adapter);
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
}
