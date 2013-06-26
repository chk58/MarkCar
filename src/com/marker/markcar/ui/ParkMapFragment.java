package com.marker.markcar.ui;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.marker.markcar.R;
import com.marker.markcar.content.MallInfo;
import com.marker.markcar.content.MapInfo;
import com.marker.markcar.map.MapView;
import com.marker.markcar.map.item.DestinationItem;
import com.marker.markcar.map.item.Map;
import com.marker.markcar.map.item.ParkingSpace;
import com.marker.markcar.map.item.SelectableItem;
import com.marker.markcar.map.item.SelectableItem.OnPressedListener;
import com.marker.markcar.map.parse.XMLMapParser;
import com.marker.markcar.prefs.SharedPrefs;

public class ParkMapFragment extends Fragment implements OnClickListener, OnItemClickListener, OnPressedListener {
    public static final String TAB_TAG = "ParkMapFragment";
    private static final TimeInterpolator INTERPOLATOR = new DecelerateInterpolator(1.5f);
    private static final int ANIMATOR_DURATION = 200;

    private SharedPrefs mSharedPrefs;
    private GridView mMapNameGridView;
    private MapNameAdapter mMapNameAdapter;
    private GridView mParkGridView;
    private ParkListAdapter mParkListAdapter;
    private MapView mMapView;
    private Map mMap;
    private int mWindowWidth;
    private Button mShowParkGrid;
    private Button mShowMap;
    private View mRootView;
    private LoadMapTask mLoadMapTask;
    private ComputePathTask mComputePathTask;
    private LayoutInflater mInflater;
    private MallInfo mMallInfo;

    private ArrayList<MapInfo> mMapInfoList;

    private Animator mLastMapAnimator;
    private boolean mMapShown = false;

    private long mCurrentMapId = -1;
    private String mCurrentParkName;
    private String mCurrentDestName;
    private TextView mParkInfo;

    public static ParkMapFragment newInstance() {
        ParkMapFragment fragment = new ParkMapFragment();
        return fragment;
    }

    public ParkMapFragment() {
    }

    public void setMallInfo(MallInfo mallInfo) {
        this.mMallInfo = mallInfo;
    }

    public void setMapInfoList(ArrayList<MapInfo> mapInfoList) {
        this.mMapInfoList = mapInfoList;
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
        mMapNameGridView.setOnItemClickListener(this);
        mParkGridView = (GridView) mRootView.findViewById(R.id.park_grid);
        mParkGridView.setOnItemClickListener(this);
        mMapView = (MapView) mRootView.findViewById(R.id.map);
        mShowParkGrid = (Button) mRootView.findViewById(R.id.show_park_grid);
        mShowParkGrid.setOnClickListener(this);
        mShowMap = (Button) mRootView.findViewById(R.id.show_map);
        mShowMap.setOnClickListener(this);
        mParkInfo = (TextView) mRootView.findViewById(R.id.park_info);
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
                v.setGravity(Gravity.CENTER);
            } else {
                v = (TextView) convertView;
            }
            MapInfo mi = mMapInfoList.get(position);
            if (mi.isShown()) {
                v.setBackgroundResource(android.R.color.holo_green_light);
                v.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                v.setBackgroundResource(0);
                v.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
            v.setText(mi.getName());
            return v;
        }
    }

    private class ParkListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mMap.getParkList().size();
        }

        @Override
        public Object getItem(int position) {
            return mMap.getParkList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            if (convertView == null) {
                v = mInflater.inflate(R.layout.park_grid_item, null);
            } else {
                v = convertView;
            }
            ParkingSpace ps = mMap.getParkList().get(position);
            TextView tv = (TextView) v.findViewById(R.id.park_name);
            if (ps.isSelected()) {
                tv.setBackgroundResource(android.R.color.holo_green_light);
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                tv.setBackgroundResource(0);
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
            tv.setText(ps.getName());
            return v;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInflater = LayoutInflater.from(getActivity());
        mSharedPrefs = SharedPrefs.getInstance(getActivity());
        Point p = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(p);
        mWindowWidth = p.x;
        mMapNameGridView.setColumnWidth(mWindowWidth / mMapInfoList.size());
        mMapNameAdapter = new MapNameAdapter();
        mMapNameGridView.setAdapter(mMapNameAdapter);
        mParkInfo.setText("You are now in " + mMallInfo.getName());
        showMap(0);
        Log.d("chk", "onActivityCreated");
    }

    private void showMap(int position) {
        MapInfo mi = mMapInfoList.get(position);
        if (!mi.isShown()) {
            for (MapInfo temp : mMapInfoList) {
                if (temp.isShown()) {
                    temp.setShown(false);
                }
            }
            mi.setShown(true);
            mCurrentMapId = mi.getId();
            mMapNameAdapter.notifyDataSetChanged();
            if (mLoadMapTask != null) {
                mLoadMapTask.cancel(true);
            }
            mLoadMapTask = new LoadMapTask(this);
            mLoadMapTask.execute(mi.getXMLFileName());
        }
    }

    private void onMapLoaded(Map map) {
        mMap = map;
        mMapView.setMap(map);
        mMapView.setOnPressedListener(this);
        mParkListAdapter = new ParkListAdapter();
        mParkGridView.setAdapter(mParkListAdapter);
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
            mFragment.onMapLoaded(map);
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

    private void selectPark(int position) {
        ParkingSpace ps = mMap.getParkList().get(position);
        ps.onPressed(this);
    }

    private MapInfo findMapInfoById(long id) {
        for (MapInfo mi : mMapInfoList) {
            if (mi.getId() == id) {
                return mi;
            }
        }
        return null;
    }

    private ParkingSpace findParkByName(String name) {
        for (ParkingSpace ps : mMap.getParkList()) {
            if (ps.getName().equals(name)) {
                return ps;
            }
        }
        return null;
    }

    private DestinationItem findDestinationByName(String name) {
        for (DestinationItem di : mMap.getDestinationList()) {
            if (di.getName().equals(name)) {
                return di;
            }
        }
        return null;
    }

    private void onParkSelected(ParkingSpace ps) {
        mSharedPrefs.setSelectedParkId(mMallInfo.getId(), mCurrentMapId, ps.getName());
        mMapView.refreshMap();
        mParkListAdapter.notifyDataSetChanged();
        mParkInfo.setText("you selected " + mMallInfo.getName() + " "
                + findMapInfoById(mCurrentMapId).getName() + " " + ps.getName());

        if (mCurrentDestName == null) {
            mMap.clearPath();
        } else {
            DestinationItem di = findDestinationByName(mCurrentDestName);
            if (di == null) {
                mMap.clearPath();
            } else {
                mMap.resetPath(ps, di);

                if (mComputePathTask != null) {
                    mComputePathTask.cancel(true);
                }
                mComputePathTask = new ComputePathTask(this);
                mComputePathTask.execute();
            }
        }
    }

    private void onDestinationSelected(DestinationItem di) {
        mMapView.refreshMap();
        mMap.clearPath();

        if (mCurrentParkName == null) {
            mMap.clearPath();
        } else {
            ParkingSpace ps = findParkByName(mCurrentParkName);
            if (ps == null) {
                mMap.clearPath();
            } else {
                mMap.resetPath(ps, di);

                if (mComputePathTask != null) {
                    mComputePathTask.cancel(true);
                }
                mComputePathTask = new ComputePathTask(this);
                mComputePathTask.execute();
            }
        }
    }

    private static class ComputePathTask extends AsyncTask<Void, Void, Void> {

        private ParkMapFragment mFragment;

        private ComputePathTask(ParkMapFragment fragment) {
            mFragment = fragment;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            mFragment.mMap.computePath();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mFragment.mMapView.refreshMap();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        switch(parent.getId()) {
            case R.id.map_name_grid:
                showMap(position);
                break;
            case R.id.park_grid:
                selectPark(position);
                break;
        }
    }

    @Override
    public void OnPressed(SelectableItem item) {
        if (item instanceof ParkingSpace) {
            final ParkingSpace ps = (ParkingSpace) item;
            if (!ps.isSelected()) {
                for (ParkingSpace temp : mMap.getParkList()) {
                    temp.setSelected(false);
                }
                ps.setSelected(true);
                mCurrentParkName = ps.getName();
                onParkSelected(ps);
            }
        } else if (item instanceof DestinationItem) {
            final DestinationItem di = (DestinationItem) item;
            if (!di.isSelected()) {
                for (DestinationItem temp : mMap.getDestinationList()) {
                    temp.setSelected(false);
                }
                di.setSelected(true);
                mCurrentDestName = di.getName();
                onDestinationSelected(di);
            }
        }
    }
}
