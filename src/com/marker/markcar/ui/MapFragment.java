package com.marker.markcar.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marker.markcar.R;

public class MapFragment extends Fragment {
    public static final String TAB_TAG = "MapFragment";
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    public MapFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("chk", "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("chk", "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("chk", "onCreateView");
        View v = inflater.inflate(R.layout.fragment_mall_park_map, container, false);


        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("chk", "onViewCreated");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("chk", "onActivityCreated");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("chk", "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("chk", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("chk", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("chk", "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("chk", "MapFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("chk", "MapFragment onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("chk", "MapFragment onDetach");
    }
}
