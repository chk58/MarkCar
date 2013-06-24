package com.marker.markcar.content;

public class Mall {
    private final int mId;
    private final String mName;

    public Mall(int id, String name) {
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

}
