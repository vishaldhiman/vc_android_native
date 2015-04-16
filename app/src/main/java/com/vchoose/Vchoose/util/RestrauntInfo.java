package com.vchoose.Vchoose.util;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.vchoose.Vchoose.R;

import java.util.ArrayList;

/**
 * Created by Archie on 04/06/2015.
 */
public class RestrauntInfo extends ActionBarActivity {

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_restraunt_info);
    Bundle extras = getIntent().getExtras();
    ArrayList<String> stringList = extras.getStringArrayList("RestrauntInfo");
    TextView textview = (TextView)findViewById(R.id.restruant_id);
    TextView textview2=(TextView)findViewById(R.id.location);
    TextView textview3=(TextView)findViewById(R.id.DishPhone);

    textview.setText(stringList.get(0));
    textview2.setText(stringList.get(2));
    textview3.setText(stringList.get(1));
}

}
