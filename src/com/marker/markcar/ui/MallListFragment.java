package com.marker.markcar.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import com.marker.markcar.R;
import com.marker.markcar.content.Mall;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Use the
 * {@link MallListFragment#newInstance} factory method to create an instance of
 * this fragment.
 * 
 */
public class MallListFragment extends ListFragment implements OnItemClickListener {
    public static final String TAB_TAG = "MallListFragment";
    private static final int WHAT_QUERY = 0;
    private EditText mSearchBox;
    private LoadMallList mLoadMallList;

    private QueryHandler mHandler;
    public static MallListFragment newInstance() {
        MallListFragment fragment = new MallListFragment();
        return fragment;
    }

    public MallListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mall_list, container, false);
        mSearchBox = (EditText) root.findViewById(R.id.search_mall);
        mSearchBox.addTextChangedListener(new TextWatcher() {

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
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler = new QueryHandler(this);
        getListView().setOnItemClickListener(this);
        loadMalls();
    }

    private void loadMalls() {
        loadMalls(null);
    }

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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void showList(ArrayList<Mall> result) {
        MallListAdapter adapter = new MallListAdapter(getActivity(), result);
        setListAdapter(adapter);
    }

    private static class QueryHandler extends Handler {

        private MallListFragment mFragment;

        private QueryHandler(MallListFragment fragment) {
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
            sList.add(new Mall(2, "北京大悦城"));
            sList.add(new Mall(3, "上海大悦城"));
            sList.add(new Mall(4, "大悦城04"));
            sList.add(new Mall(5, "大悦城05"));
            sList.add(new Mall(6, "大悦城06"));
            sList.add(new Mall(7, "大悦城07"));
            sList.add(new Mall(8, "大悦城08"));
            sList.add(new Mall(9, "大悦城09"));
            sList.add(new Mall(10, "大悦城10"));
            sList.add(new Mall(11, "大悦城11"));
        }
        private MallListFragment mFragment;

        private LoadMallList(MallListFragment fragment) {
            mFragment = fragment;
        }

        @Override
        protected void onPostExecute(ArrayList<Mall> result) {
            mFragment.showList(result);
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

    }
}
