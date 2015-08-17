package com.vchoose.Vchoose;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.vchoose.Vchoose.R;

import java.util.ArrayList;

public class WelcomeFragment extends Fragment {

    ListView myList;

    public WelcomeFragment() {
        // Required empty public constructor
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
        welcomeArray.add("first");
        welcomeArray.add("second");
        welcomeArray.add("third");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, welcomeArray);
        myList.setAdapter(adapter);
        return v;
    }

}
