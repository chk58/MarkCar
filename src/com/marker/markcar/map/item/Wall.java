package com.marker.markcar.map.item;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class Wall extends MapItem {
    private static final Paint WALL_PAINT = new Paint();
    private static final float WIDTH = 100;
    static {
        WALL_PAINT.setStrokeWidth(WIDTH);
        WALL_PAINT.setColor(Color.BLACK);
        WALL_PAINT.setStyle(Style.STROKE);
    }

    private final boolean mIsVertical;

    public Wall(float x1, float y1, float x2, float y2, int degree) {
        if (x1 == x2) {
            float top, bottom;
            if (y1 < y2) {
                top = y1;
                bottom = y2;
            } else {
                top = y2;
                bottom = y1;
            }
            mBounds = new RectF(x1 - WIDTH / 2, top, x2 + WIDTH / 2, bottom);
            mIsVertical = true;
        } else if (y1 == y2) {
            float left, right;
            if (x1 < x2) {
                left = x1;
                right = x2;
            } else {
                left = x2;
                right = x1;
            }
            mBounds = new RectF(left, y1 - WIDTH / 2, right, y2 + WIDTH / 2);
            mIsVertical = false;
        } else {
            throw new IllegalArgumentException();
        }

        mDegree = degree;
        mPaint = WALL_PAINT;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate(mDegree, mBounds.centerX(), mBounds.centerY());
         if (mIsVertical) {
             canvas.drawLine(mBounds.centerX(), mBounds.top, mBounds.centerX(), mBounds.bottom, mPaint);
         } else {
             canvas.drawLine(mBounds.left, mBounds.centerY(), mBounds.right, mBounds.centerY(), mPaint);
         }
        canvas.restore();
    }
}
