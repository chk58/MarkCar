package com.marker.markcar.map.item;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public abstract class MapItem {
    protected Paint mPaint;
    protected int mDegree;
    protected RectF mBounds;

    public abstract void draw(Canvas canvas);

    public boolean contains(float x, float y) {
        boolean result = false;
        float rx = mBounds.left;
        float ry = mBounds.top;
        float width = mBounds.right - mBounds.left;
        float height = mBounds.bottom - mBounds.top;
        if (mDegree == 0) {
            result = ((rx <= x) && (x <= (rx + width)) && (ry <= y) && (y <= (ry + height)));
        } else {
            double r = Math.sqrt((y - ry) * (y - ry) + (x - rx) * (x - rx));
            double degree1 = 0;
            if (y != ry) {
                degree1 = Math.atan((x - rx) / (y - ry)) * 180 / Math.PI;
            }
            double radian = (degree1 + mDegree) * Math.PI / 180;
            double sin = Math.sin(radian);
            double cos = Math.cos(radian);
            float ox = (float) (r * sin) + rx;
            float oy = (float) (r * cos) + ry;
            result = ((rx <= ox) && (ox <= (rx + width)) && (ry <= oy) && (oy <= (ry + height)));
        }
        return result;
    }
}