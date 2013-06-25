package com.marker.markcar.ui;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
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
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.marker.markcar.R;
import com.marker.markcar.content.MallInfo;
import com.marker.markcar.content.MapInfo;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Use the
 * {@link MallMapParkFragment#newInstance} factory method to create an instance of
 * this fragment.
 *
 */
public class MallMapParkFragment extends ListFragment implements OnItemClickListener, BackKeyPressAble {
    public static final String TAB_TAG = "MallMapParkFragment";
    private static final int WHAT_QUERY = 0;
    private static final TimeInterpolator INTERPOLATOR = new DecelerateInterpolator(1.5f);
    private static final int ANIMATOR_DURATION = 200;

    private LoadMallListTask mLoadMallListTask;
    private LoadMapListTask mLoadMapListTask;
    private QueryHandler mHandler;
    private Animator mLastParkMapAnimator;
    private FragmentManager mFragmentManager;

    private View mRootView;
    private View mParkMapContainer;
    private View mParkMapContent;
    private EditText mEditText;
    private TextView mLoadInfo;
    private ParkMapFragment mParkMapFragment;
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

        mEditText = ((EditText) mRootView.findViewById(R.id.search_mall));
        mEditText.addTextChangedListener(new TextWatcher() {
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
        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                }
            }
        });
        mParkMapContainer = mRootView.findViewById(R.id.park_map_container);
        mParkMapContent = mRootView.findViewById(R.id.park_map_content);
        mLoadInfo = (TextView) mRootView.findViewById(R.id.load_info);
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
        ((View) mEditText.getParent()).requestFocus();
        mFragmentManager = getFragmentManager();
        mHandler = new QueryHandler(this);
        getListView().setOnItemClickListener(this);
        loadMalls();
        slideParkMapLayout(1920);

        Log.d("chk", "onActivityCreated");
    }

    private void loadMalls() {
        loadMalls(null);
    }

    private void loadMalls(String query) {
        if (mLoadMallListTask != null) {
            mLoadMallListTask.cancel(true);
        }
        mLoadMallListTask = new LoadMallListTask(this);
        mLoadMallListTask.execute(query);
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

    private void showMallList(ArrayList<MallInfo> result) {
        if (isVisible()) {
            MallListAdapter adapter = new MallListAdapter(getActivity(), result);
            setListAdapter(adapter);
        }
    }

    public void showParkMapLayout() {
        if (mParkMapShown) {
            return;
        }

        mParkMapShown = true;
        mParkMapContainer.setVisibility(View.VISIBLE);
        mParkMapContainer.requestFocus();
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

    private static class LoadMallListTask extends AsyncTask<String, Void, ArrayList<MallInfo>> {

        private static final ArrayList<MallInfo> sList = new ArrayList<MallInfo>();
        {
            sList.clear();
            sList.add(new MallInfo(0, "西单大悦城"));
            sList.add(new MallInfo(1, "朝阳大悦城"));
            sList.add(new MallInfo(2, "安定门大悦城"));
            sList.add(new MallInfo(3, "沈阳大悦城"));
            sList.add(new MallInfo(4, "天津大悦城"));
            sList.add(new MallInfo(5, "上海大悦城"));
            sList.add(new MallInfo(6, "大悦城06"));
            sList.add(new MallInfo(7, "大悦城07"));
            sList.add(new MallInfo(8, "大悦城08"));
            sList.add(new MallInfo(9, "大悦城09"));
            sList.add(new MallInfo(10, "大悦城10"));
            sList.add(new MallInfo(11, "大悦城11"));
        }
        private MallMapParkFragment mFragment;

        private LoadMallListTask(MallMapParkFragment fragment) {
            mFragment = fragment;
        }

        @Override
        protected void onPostExecute(ArrayList<MallInfo> result) {
            mFragment.showMallList(result);
        }

        @Override
        protected ArrayList<MallInfo> doInBackground(String... params) {
            if (params == null || params.length == 0 || TextUtils.isEmpty(params[0])) {
                return sList;
            } else {
                String query = params[0];
                ArrayList<MallInfo> list = new ArrayList<MallInfo>();
                for (MallInfo m : sList) {
                    if (m.getName() != null && m.getName().contains(query)) {
                        list.add(m);
                    }
                }
                return list;
            }
        }
    }

    private static class LoadMapListTask extends AsyncTask<Long, Void, ArrayList<MapInfo>> {

        private static final ArrayList<MapInfo> sList = new ArrayList<MapInfo>();
        {
            sList.clear();
            sList.add(new MapInfo(0, 0, "B1", "map_01.xml"));
            sList.add(new MapInfo(1, 0, "B2", "map_01.xml"));
            sList.add(new MapInfo(2, 0, "B3", "map_01.xml"));
            sList.add(new MapInfo(3, 1, "B1", "map_01.xml"));
            sList.add(new MapInfo(4, 1, "B2", "map_01.xml"));
            sList.add(new MapInfo(5, 2, "B1", "map_01.xml"));
            sList.add(new MapInfo(6, 2, "B2", "map_01.xml"));
            sList.add(new MapInfo(7, 2, "B3", "map_01.xml"));
            sList.add(new MapInfo(8, 3, "B1", "map_01.xml"));
        }

        private MallMapParkFragment mFragment;

        private LoadMapListTask(MallMapParkFragment fragment) {
            mFragment = fragment;
        }

        @Override
        protected void onPreExecute() {
            mFragment.mLoadInfo.setVisibility(View.VISIBLE);
            mFragment.mParkMapContent.setVisibility(View.GONE);

            Fragment f = mFragment.mFragmentManager.findFragmentByTag(ParkMapFragment.TAB_TAG);
            if (f != null) {
                FragmentTransaction ft = mFragment.mFragmentManager.beginTransaction();
                ft.remove(f);
                ft.commit();
            }

            mFragment.mLoadInfo.setText("Loading...");
            mFragment.showParkMapLayout();
        }

        @Override
        protected void onPostExecute(ArrayList<MapInfo> result) {
            if (result == null || result.isEmpty()) {
                mFragment.mLoadInfo.setText("No map for this mall.");
                return;
            }
            mFragment.showParkMap(result);
        }

        @Override
        protected ArrayList<MapInfo> doInBackground(Long... params) {
            ArrayList<MapInfo> list = new ArrayList<MapInfo>();
            if (params != null && params.length > 0 && params[0] != null) {
                long mallId = params[0].longValue();
                for (MapInfo map : sList) {
                    if (map.getMallId() == mallId) {
                        list.add(map);
                    }
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            return list;
        }
    }

    private void showParkMap(ArrayList<MapInfo> data) {
        mLoadInfo.setVisibility(View.GONE);
        mParkMapContent.setVisibility(View.VISIBLE);

        mParkMapFragment = new ParkMapFragment();
        mParkMapFragment.setMapInfoList(data);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.park_map_content, mParkMapFragment, ParkMapFragment.TAB_TAG);
        ft.commit();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mLoadMapListTask != null) {
            mLoadMapListTask.cancel(true);
        }
        mLoadMapListTask = new LoadMapListTask(this);
        mLoadMapListTask.execute(id);
    }

    @Override
    public boolean onBackPressed() {
        if (mParkMapShown) {
            hideParkMapLayout();
            return true;
        }
        return false;
    }
}