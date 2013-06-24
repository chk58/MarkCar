package com.marker.markcar.map.item;

import android.graphics.Paint;

public abstract class SelectableItem extends MapItem {

    protected boolean mIsSelected = false;
    protected Paint mSelectPaint;

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }
}
