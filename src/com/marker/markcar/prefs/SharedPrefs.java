package com.marker.markcar.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
    private static SharedPrefs mInstance = null;
    /** SharedPreferences. */
    private final SharedPreferences mSharedPrefs;
    /** Preference file name. */
    private static final String PREFS_NAME = "customize_prefs";
    private static final String KEY_SELECTED_PARK = "selected_park_id";
    public static final String SEPARATOR = "-";

    /**
     * Get CustomizePreferences.
     * @param context Context.
     * @return SharedPrefs instance.
     */
    public static synchronized SharedPrefs getInstance(final Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefs(context);
        }
        return mInstance;
    }

    /**
     * Constructor.
     * @param context Context.
     */
    private SharedPrefs(final Context context) {
        mSharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setSelectedParkId(long mallId, long mapId, String parkName) {
        StringBuilder value = new StringBuilder();
        value.append(mallId).append(SEPARATOR).append(mapId).append(SEPARATOR).append(parkName);
        setValue(KEY_SELECTED_PARK, value.toString());
    }

    public String getSelectedParkId() {
        return getValue(KEY_SELECTED_PARK);
    }

    private void setValue(final String key, final String value) {
        mSharedPrefs.edit().putString(key, value).commit();
    }

    private String getValue(final String key) {
        String data = null;
        try {
            data = mSharedPrefs.getString(key, null);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return data;
    }
}
