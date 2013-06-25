package com.marker.markcar.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.marker.markcar.R;

public class ParkGridFragment extends Fragment {
    public static final String TAB_TAG = "ParkGridFragment";

    private GridView mParkGridView;

    public static ParkGridFragment newInstance() {
        ParkGridFragment fragment = new ParkGridFragment();
        return fragment;
    }

    public ParkGridFragment() {
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
        View v = inflater.inflate(R.layout.fragment_park_grid, container, false);
        mParkGridView = (GridView) v.findViewById(R.id.park_grid);
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
        Log.d("chk", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("chk", "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("chk", "onDetach");
    }
}
