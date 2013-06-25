package com.marker.markcar.content;

public class MallInfo {
    private final long mId;
    private final String mName;

    public MallInfo(long id, String name) {
        mId = id;
        mName = name;
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

}
