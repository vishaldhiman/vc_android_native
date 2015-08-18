package com.vchoose.Vchoose;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.vchoose.Vchoose.R;

import java.util.ArrayList;

public class WelcomeFragment extends Fragment {

    ListView myList;
    ButtonRectangle buttonRectangle;
    Button button;
    AutoCompleteTextView textView;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public void setButtonRectangle(ButtonRectangle buttonRectangle) {
        this.buttonRectangle = buttonRectangle;
    }

    public void setTextView(AutoCompleteTextView textView) {
        this.textView = textView;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);

        myList=(ListView)v.findViewById(R.id.myListView);
        ArrayList<String> welcomeArray = new ArrayList<>();
        welcomeArray.add("What's hot!");
        welcomeArray.add("Top Trending");
        welcomeArray.add("Top Trending Custom Dishes");
        welcomeArray.add("Vegan Dishes near me");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.welcome_list_item, android.R.id.text1, welcomeArray);
        myList.setAdapter(adapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        textView.setText("whats hot");
                        button.performClick();
                        break;
                    case 1:
                        textView.setText("top trending");
                        button.performClick();
                        break;
                    case 2:
                        textView.setText("top trending custom");
                        button.performClick();
                        break;
                    case 3:
                        textView.setText("vegan");
                        button.performClick();
                        break;
                }
            }
        });

        return v;
    }

}
