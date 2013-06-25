package com.marker.markcar.ui;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.marker.markcar.R;
import com.marker.markcar.content.MapInfo;
import com.marker.markcar.map.MapView;
import com.marker.markcar.map.item.Map;
import com.marker.markcar.map.parse.XMLMapParser;

public class ParkMapFragment extends Fragment implements OnClickListener {
    public static final String TAB_TAG = "ParkMapFragment";
    private static final TimeInterpolator INTERPOLATOR = new DecelerateInterpolator(1.5f);
    private static final int ANIMATOR_DURATION = 200;
    private GridView mMapNameGridView;
    private GridView mParkGridView;
    private MapView mMapView;
    private int mWindowWidth;
    private Button mShowParkGrid;
    private Button mShowMap;
    private View mRootView;
    private LoadMapTask mLoadMapTask;

    private ArrayList<MapInfo> mMapInfoList;

    private Animator mLastMapAnimator;
    private boolean mMapShown = false;
    public static ParkMapFragment newInstance() {
        ParkMapFragment fragment = new ParkMapFragment();
        return fragment;
    }

    public ParkMapFragment() {
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
        mRootView = inflater.inflate(R.layout.fragment_park_map, container, false);
        mMapNameGridView = (GridView) mRootView.findViewById(R.id.map_name_grid);
        mParkGridView = (GridView) mRootView.findViewById(R.id.park_grid);
        mMapView = (MapView) mRootView.findViewById(R.id.map);
        mShowParkGrid = (Button) mRootView.findViewById(R.id.show_park_grid);
        mShowParkGrid.setOnClickListener(this);
        mShowMap = (Button) mRootView.findViewById(R.id.show_map);
        mShowMap.setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("chk", "onViewCreated");
    }

    private class MapNameAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mMapInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mMapInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mMapInfoList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView v;
            if (convertView == null) {
                v = new TextView(getActivity());
            } else {
                v = (TextView) convertView;
            }
            v.setText(mMapInfoList.get(position).getName());
            return v;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Point p = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(p);
        mWindowWidth = p.x;
        mMapNameGridView.setColumnWidth(mWindowWidth / mMapInfoList.size());
        mMapNameGridView.setAdapter(new MapNameAdapter());
        loadMap(mMapInfoList.get(0).getXMLFileName());
        Log.d("chk", "onActivityCreated");
    }

    private void loadMap(String fileName) {
        if (mLoadMapTask != null) {
            mLoadMapTask.cancel(true);
        }
        mLoadMapTask = new LoadMapTask(this);
        mLoadMapTask.execute(fileName);
    }

    private static class LoadMapTask extends AsyncTask<String, Void, Map> {

        private ParkMapFragment mFragment;

        private LoadMapTask(ParkMapFragment fragment) {
            mFragment = fragment;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Map map) {
            mFragment.mMapView.setMap(map);
        }

        @Override
        protected Map doInBackground(String... params) {
            return (new XMLMapParser(mFragment.getActivity(), params[0])).parse();
        }
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

    public void showMapLayout() {
        if (mMapShown) {
            return;
        }

        mMapShown = true;
        slideParkGridLayout(mRootView.getWidth());
    }

    public void hideMapLayout() {
        if (!mMapShown) {
            return;
        }
        mMapShown = false;
        slideParkGridLayout(0);
    }

    public void setParkGridLayoutXAnim(int x) {
        if (mParkGridView != null) {
            ((ViewGroup.MarginLayoutParams) mParkGridView.getLayoutParams()).leftMargin = x;
            mParkGridView.requestLayout();
        }
    }

    private void slideParkGridLayout(int toX) {
        if (mParkGridView == null)
            return;
        if (mLastMapAnimator != null) {
            mLastMapAnimator.cancel();
        }

        final PropertyValuesHolder[] values = { PropertyValuesHolder.ofInt("ParkGridLayoutXAnim",
                ((ViewGroup.MarginLayoutParams) mParkGridView.getLayoutParams()).leftMargin, toX) };
        final ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(this, values).setDuration(
                ANIMATOR_DURATION);
        animator.setInterpolator(INTERPOLATOR);
        mLastMapAnimator = animator;
        animator.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_park_grid:
                hideMapLayout();
                break;
            case R.id.show_map:
                showMapLayout();
                break;
        }
    }

    public void setMapInfoList(ArrayList<MapInfo> mapInfoList) {
        this.mMapInfoList = mapInfoList;
    }
}
