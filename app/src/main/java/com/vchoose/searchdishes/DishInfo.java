package com.vchoose.searchdishes;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;


public class DishInfo extends ActionBarActivity {

    public static String NAME = "";
    public static String DIS = "";
    //public static ArrayList<String> stringList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_info);

    }

    public void customize(View view) {
        Intent intent = new Intent(this, Customization.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dish_info, menu);
        Intent intent = getIntent();
        TextView textview = (TextView)findViewById(R.id.DishName);
        TextView textview2=(TextView)findViewById(R.id.DishPhone);
        TextView textview3=(TextView)findViewById(R.id.DishDiscribe);
        //ArrayList<String> stringList = (ArrayList<String>) getIntent().getStringArrayListExtra("ListString");
        //textview.setText(intent.getStringExtra(stringList.get(0)));
        //textview3.setText(intent.getStringExtra(stringList.get(1)));
        textview.setText(intent.getStringExtra(DishInfo.NAME));
        //textview3.setText(intent.getStringExtra(DishInfo.DIS));
        textview2.setText("(412)548-5979");
        textview3.setText("a custom one made to order. choose from any of our delicious crust styles, including handmade pan.");
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

        return super.onOptionsItemSelected(item);
    }
}
