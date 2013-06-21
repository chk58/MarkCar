package com.marker.markcar.map.item;

import java.util.ArrayList;

import android.graphics.Canvas;

import com.marker.markcar.map.path.Path;

public class Map {

    private final ArrayList<MapItem> mItemList;
    private final ArrayList<Selectable> mSelectableList;

    private final float mWidth;
    private final float mHeight;
    private Path mPath;
    public Map(ArrayList<MapItem> itemList, ArrayList<Selectable> selectableList, float width, float height) {
        mItemList = itemList;
        mSelectableList = selectableList;
        mWidth = width;
        mHeight = height;
        mPath = new Path((MapItem) mSelectableList.get(0), (MapItem) mSelectableList.get(30), mItemList);
        ((Selectable) mSelectableList.get(0)).setSelected(true);
        ((Selectable) mSelectableList.get(30)).setSelected(true);
        mPath.computePath();
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

    public ArrayList<Selectable> getSelectableList() {
        return mSelectableList;
    }
}
