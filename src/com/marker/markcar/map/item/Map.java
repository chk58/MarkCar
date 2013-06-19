package com.marker.markcar.map.item;

import java.util.ArrayList;

import android.graphics.Canvas;

public class Map {

    private final ArrayList<MapItem> mItemList;
    private final float mWidth;
    private final float mHeight;

    public Map(ArrayList<MapItem> itemList, float width, float height) {
        mItemList = itemList;
        mWidth = width;
        mHeight = height;
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
    }
}
