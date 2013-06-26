package com.marker.markcar.content;

public class MapInfo {
    private final long mId;
    private final long mMallId;
    private final String mName;
    private final String mXMLFileName;
    private boolean mShown;

    public MapInfo(long id, long mallId, String name, String XMLFileName) {
        mId = id;
        mMallId = mallId;
        mName = name;
        mXMLFileName = XMLFileName;
    }

    public long getId() {
        return mId;
    }

    public long getMallId() {
        return mMallId;
    }

    public String getName() {
        return mName;
    }

    public String getXMLFileName() {
        return mXMLFileName;
    }

    public boolean isShown() {
        return mShown;
    }

    public void setShown(boolean shown) {
        this.mShown = shown;
    }

}
