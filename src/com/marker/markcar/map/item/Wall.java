package com.marker.markcar.map.item;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class Wall extends MapItem {
    private static final Paint WALL_PAINT = new Paint();
    static {
        WALL_PAINT.setStrokeWidth(0);
        WALL_PAINT.setColor(Color.BLACK);
        WALL_PAINT.setStyle(Style.STROKE);
    }

    public Wall(float x1, float y1, float x2, float y2, int degree) {
        mBounds = new RectF(x1, y1, x2, y2);
        mDegree = degree;
        mPaint = WALL_PAINT;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate(mDegree, mBounds.left, mBounds.top);
        canvas.drawLine(mBounds.left, mBounds.top, mBounds.right, mBounds.bottom, mPaint);
        canvas.restore();
    }
}
