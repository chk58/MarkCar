package com.marker.markcar.ui;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.marker.markcar.R;
import com.marker.markcar.content.Mall;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Use the
 * {@link MallMapParkFragment#newInstance} factory method to create an instance of
 * this fragment.
 *
 */
public class MallMapParkFragment extends ListFragment implements OnItemClickListener, BackKeyPressAble, OnClickListener {
    public static final String TAB_TAG = "MallMapParkFragment";
    private static final int WHAT_QUERY = 0;
    private static final TimeInterpolator INTERPOLATOR = new DecelerateInterpolator(1.5f);
    private static final int ANIMATOR_DURATION = 200;

    private LoadMallList mLoadMallList;
    private QueryHandler mHandler;
    private Animator mLastParkMapAnimator;
    private FragmentManager mFragmentManager;

    private View mRootView;
    private View mParkMapContainer;
    private ParkGridFragment mParkGridFragment;
    private MapFragment mMapFragment;
    private Button mShowParkGrid;
    private Button mShowMap;
    private boolean mParkMapShown = false;
    public static MallMapParkFragment newInstance() {
        MallMapParkFragment fragment = new MallMapParkFragment();
        return fragment;
    }

    public MallMapParkFragment() {
        // Required empty public constructor
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
        mRootView = inflater.inflate(R.layout.fragment_mall_park_map, container, false);

        ((EditText) mRootView.findViewById(R.id.search_mall)).addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHandler.removeMessages(WHAT_QUERY);
                Message m = mHandler.obtainMessage(WHAT_QUERY, s);
                mHandler.sendMessageDelayed(m, 500);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mParkMapContainer = mRootView.findViewById(R.id.park_map_container);
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

    @SuppressLint("NewApi")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManager = getFragmentManager();
        mHandler = new QueryHandler(this);
        getListView().setOnItemClickListener(this);
        loadMalls();
        slideParkMapLayout(1080);
        Log.d("chk", "onActivityCreated");
    }

    private void loadMalls() {
        loadMalls(null);
    }

    //public boolean

    private void loadMalls(String query) {
        if (mLoadMallList != null) {
            mLoadMallList.cancel(true);
        }
        mLoadMallList = new LoadMallList(this);
        mLoadMallList.execute(query);
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
        mParkMapShown = false;
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

    private void showMallList(ArrayList<Mall> result) {
        if (isVisible()) {
            MallListAdapter adapter = new MallListAdapter(getActivity(), result);
            setListAdapter(adapter);
        }
    }

    public void showParkMapLayout(long id) {
        if (mParkMapShown) {
            return;
        }

        mParkMapShown = true;
        mParkMapContainer.setVisibility(View.VISIBLE);
        slideParkMapLayout(0);
    }

    public void hideParkMapLayout() {
        if (!mParkMapShown) {
            return;
        }
        mParkMapShown = false;
        slideParkMapLayout(mRootView.getHeight());
    }

    public void setParkMapLayoutYAnim(int y) {
        if (mParkMapContainer != null) {
            ((ViewGroup.MarginLayoutParams) mParkMapContainer.getLayoutParams()).topMargin = y;
            mParkMapContainer.requestLayout();
        }
    }

    private void slideParkMapLayout(int toY) {
        if (mParkMapContainer == null) return;
        if (mLastParkMapAnimator != null) {
            mLastParkMapAnimator.cancel();
        }

        final PropertyValuesHolder[] values = {
            PropertyValuesHolder.ofInt("ParkMapLayoutYAnim",
                    ((ViewGroup.MarginLayoutParams)mParkMapContainer.getLayoutParams()).topMargin,
                    toY)
        };
        final ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(this, values)
                .setDuration(ANIMATOR_DURATION);
        animator.setInterpolator(INTERPOLATOR);
        mLastParkMapAnimator = animator;
        animator.start();
    }

    private static class QueryHandler extends Handler {

        private MallMapParkFragment mFragment;

        private QueryHandler(MallMapParkFragment fragment) {
            mFragment = fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            mFragment.loadMalls(msg.obj.toString().trim());
        }
    }

    private static class LoadMallList extends AsyncTask<String, Void, ArrayList<Mall>> {

        private static final ArrayList<Mall> sList = new ArrayList<Mall>();
        {
            sList.clear();
            sList.add(new Mall(0, "西单大悦城"));
            sList.add(new Mall(1, "朝阳大悦城"));
            sList.add(new Mall(2, "安定门大悦城"));
            sList.add(new Mall(3, "沈阳大悦城"));
            sList.add(new Mall(4, "天津大悦城"));
            sList.add(new Mall(5, "上海大悦城"));
            sList.add(new Mall(6, "大悦城06"));
            sList.add(new Mall(7, "大悦城07"));
            sList.add(new Mall(8, "大悦城08"));
            sList.add(new Mall(9, "大悦城09"));
            sList.add(new Mall(10, "大悦城10"));
            sList.add(new Mall(11, "大悦城11"));
        }
        private MallMapParkFragment mFragment;

        private LoadMallList(MallMapParkFragment fragment) {
            mFragment = fragment;
        }

        @Override
        protected void onPostExecute(ArrayList<Mall> result) {
            mFragment.showMallList(result);
        }

        @Override
        protected ArrayList<Mall> doInBackground(String... params) {
            if (params == null || params.length == 0 || TextUtils.isEmpty(params[0])) {
                return sList;
            } else {
                String query = params[0];
                ArrayList<Mall> list = new ArrayList<Mall>();
                for (Mall m : sList) {
                    if (m.getName() != null && m.getName().contains(query)) {
                        list.add(m);
                    }
                }
                return list;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showParkMapLayout(id);
    }

    @Override
    public boolean onBackPressed() {
        if (mParkMapShown) {
            hideParkMapLayout();
            return true;
        }
        return false;
    }

    private void showParkGridFragment() {
//        mShowMap.setBackgroundResource(android.R.drawable.btn_default);
//        mShowParkGrid.setBackgroundResource(android.R.drawable.btn_star_big_on);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (mMapFragment != null && !mMapFragment.isDetached()) {
            ft.detach(mMapFragment);
        }
        if (mParkGridFragment == null) {
            mParkGridFragment = ParkGridFragment.newInstance();
            ft.add(R.id.park_map_content, mParkGridFragment, ParkGridFragment.TAB_TAG);
        } else if (mParkGridFragment.isDetached()) {
            ft.attach(mParkGridFragment);
        }
        ft.commit();
    }

    private void showMapFragment() {
//        mShowParkGrid.setBackgroundResource(android.R.drawable.btn_default);
//        mShowMap.setBackgroundResource(android.R.drawable.btn_star_big_on);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (mParkGridFragment != null && !mParkGridFragment.isDetached()) {
            ft.detach(mParkGridFragment);
        }
        if (mMapFragment == null) {
            mMapFragment = MapFragment.newInstance();
            ft.add(R.id.park_map_content, mMapFragment,
                    MapFragment.TAB_TAG);
        } else if (mMapFragment.isDetached()) {
            ft.attach(mMapFragment);
        }
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_park_grid:
                showParkGridFragment();
                break;
            case R.id.show_map:
                showMapFragment();
                break;
        }
    }
}