package com.vchoose.Vchoose;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.Pair;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by vishaldhiman on 4/29/15.
 */
public class MapsFragment extends Fragment {
    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static final LatLng KIEL = new LatLng(53.551, 9.993);
    private GoogleMap map;
    private static View v;
    ArrayList<Pair<String, LatLng>> mapMarkers;
    SupportMapFragment mapFrag;

    public void setMapMarkers(ArrayList<Pair<String, LatLng>> mMarkers) {
        mapMarkers = mMarkers;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (v != null) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null)
                parent.removeView(v);
        }
        Log.v("MapsFragment", "onCreateView");

        try {
            v = inflater.inflate(R.layout.activity_mapsfragment, container, false);


                //FragmentManager fManager = getActivity().getSupportFragmentManager();
                FragmentManager fManager = getChildFragmentManager();

                if (fManager == null) {
                    Log.v("MapsFragment", "fManager is null");
                }

            mapFrag = (SupportMapFragment) fManager.findFragmentById(R.id.map);

                if (mapFrag == null) {
                    Log.v("MapsFragment", "mapFrag is null");
                }

                map = mapFrag.getMap();

                map.setMyLocationEnabled(true);

                if ((mapMarkers != null) && (mapMarkers.size() > 0)) {
                    Log.v("MapsFragment","# of mapMarkers: "+mapMarkers.size());
                    for (int i = 0; i < mapMarkers.size() - 1 ; i++) {
                        Pair<String, LatLng> pair = mapMarkers.get(i);

                        Marker marker = map.addMarker(new MarkerOptions().position(pair.second).title(pair.first));
                    }
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapMarkers.get(0).second, 15));
                }

                /*
                Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
                        .title("Hamburg"));
                Marker kiel = map.addMarker(new MarkerOptions()
                        .position(KIEL)
                        .title("Kiel")
                        .snippet("Kiel is cool")
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.ic_launcher)));
                */
                // Move the camera instantly to hamburg with a zoom of 15.
                //map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

                // Zoom in, animating the camera.
                map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

        } catch (InflateException ie) {
            map.clear();

            map = mapFrag.getMap();

            map.setMyLocationEnabled(true);

            if ((mapMarkers != null) && (mapMarkers.size() > 0)) {
                Log.v("MapsFragment","# of mapMarkers: "+mapMarkers.size());
                for (int i = 0; i < mapMarkers.size() - 1; i++) {
                    Pair<String, LatLng> pair = mapMarkers.get(i);

                    Marker marker = map.addMarker(new MarkerOptions().position(pair.second).title(pair.first));
                }
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapMarkers.get(0).second, 15));
            }
            map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
            // map is already there
            Log.v("MapsFragment", "map is already there");
        }

        return v;
    }
}
