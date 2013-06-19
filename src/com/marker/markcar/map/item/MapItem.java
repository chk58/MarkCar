package com.marker.markcar.map.item;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public abstract class MapItem {
    public static final Paint DEFAULT_PAINT = new Paint();
    static {
        DEFAULT_PAINT.setStrokeWidth(0);
        DEFAULT_PAINT.setColor(Color.BLACK);
        DEFAULT_PAINT.setStyle(Style.STROKE);
    }
    protected Paint mPaint;
    protected int mDegree;
    protected RectF mBounds;

    public abstract void draw(Canvas canvas);
}