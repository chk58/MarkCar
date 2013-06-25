package com.marker.markcar.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentTabHost;
import android.view.Menu;

import com.marker.markcar.R;

public class MainActivity extends Activity {

    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec(MallMapParkFragment.TAB_TAG).setIndicator("车位导航"),
                MallMapParkFragment.class,
                null);

        mTabHost.addTab(mTabHost.newTabSpec("warning").setIndicator("车位预警"), Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("activity").setIndicator("优惠活动"), Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("more").setIndicator("更多..."), Fragment.class, null);
    }

    @Override
    public void onBackPressed() {
        boolean handled = false;
        Fragment f = getFragmentManager().findFragmentByTag(mTabHost.getCurrentTabTag());
        if (f instanceof BackKeyPressAble) {
            handled = ((BackKeyPressAble) f).onBackPressed();
        }
        if (!handled) {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
