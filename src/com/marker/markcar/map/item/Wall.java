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
            mBounds = new RectF(x1 - WIDTH, y1, x2 + WIDTH, y2);
            mIsVertical = true;
        } else if (y1 == y2) {
            mBounds = new RectF(x1, y1, x2, y2 + WIDTH);
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
