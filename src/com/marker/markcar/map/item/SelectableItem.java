package com.marker.markcar.map.item;

import android.graphics.Paint;

public abstract class SelectableItem extends MapItem {
    public interface OnPressedListener {
        public void OnPressed(SelectableItem item);
    }
    protected boolean mIsSelected = false;
    protected Paint mSelectPaint;

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public void onPressed(OnPressedListener listener) {
        if (listener != null) {
            listener.OnPressed(this);
        }
    }
}
