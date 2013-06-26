package com.marker.markcar.map.item;

import java.util.ArrayList;

import android.graphics.Canvas;

import com.marker.markcar.map.path.Path;

public class Map {

    private final ArrayList<MapItem> mItemList;
    private final ArrayList<SelectableItem> mSelectableList;
    private final ArrayList<ParkingSpace> mParkList;
    private final ArrayList<DestinationItem> mDestinationList;

    private final float mWidth;
    private final float mHeight;
    private Path mPath;
    public Map(ArrayList<MapItem> itemList, ArrayList<SelectableItem> selectableList,
            ArrayList<ParkingSpace> parkList, ArrayList<DestinationItem> destinationList, float width, float height) {
        mItemList = itemList;
        mSelectableList = selectableList;
        mParkList = parkList;
        mDestinationList = destinationList;
        mWidth = width;
        mHeight = height;

//        mPath = new Path(mSelectableList.get(0), mSelectableList.get(35), mItemList);
//        mSelectableList.get(0).setSelected(true);
//        mSelectableList.get(35).setSelected(true);
//        mPath.computePath();
    }

    public void clearPath() {
        mPath = null;
    }

    public void resetPath(MapItem start, MapItem end) {
        mPath = new Path(start, end, mItemList);
    }


    public boolean computePath() {
        if (mPath != null && !mPath.isComplete()) {
            return mPath.computePath();
        }
        return false;
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public void draw(Canvas canvas) {
        for (MapItem item : mItemList) {
            item.draw(canvas);
        }
        if (mPath != null && mPath.isComplete()) {
            mPath.drawPath(canvas);
        }
    }

    public ArrayList<SelectableItem> getSelectableList() {
        return mSelectableList;
    }

    public ArrayList<ParkingSpace> getParkList() {
        return mParkList;
    }

    public ArrayList<DestinationItem> getDestinationList() {
        return mDestinationList;
    }
}
