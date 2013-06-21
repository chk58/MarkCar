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
        float ox = x;
        float oy = y;
        if (mDegree > 0) {
            float rx = mBounds.centerX();
            float ry = mBounds.centerY();
            double r = Math.sqrt((y - ry) * (y - ry) + (x - rx) * (x - rx));
            double degree1 = 0;
            if (y != ry) {
                degree1 = Math.atan((x - rx) / (y - ry)) * 180 / Math.PI;
            }
            double radian = (degree1 + mDegree) * Math.PI / 180;
            double sin = Math.sin(radian);
            double cos = Math.cos(radian);
            ox = (float) (r * sin) + rx;
            oy = (float) (r * cos) + ry;
        }
        return ((mBounds.left <= ox) && (ox <= mBounds.right) && (mBounds.top <= oy) && (oy <= mBounds.bottom));
    }

    public float getCenterX() {
        return mBounds.centerX();
    }

    public float getCenterY() {
        return mBounds.centerY();
    }
}