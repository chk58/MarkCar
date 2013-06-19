package com.marker.markcar.map.item;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public abstract class MapItem {
    protected Paint mPaint;
    protected int mDegree;
    protected RectF mBounds;

    public abstract void draw(Canvas canvas);
}