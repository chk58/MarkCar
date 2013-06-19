package com.marker.markcar.map.item;

import android.graphics.Canvas;

public class Wall extends MapItem {

    private float[] mPoints;

    public Wall(float[] points, int degree) {
        mPoints = points;
        mDegree = degree;
        mPaint = DEFAULT_PAINT;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate(mDegree, mPoints[0], mPoints[1]);
        canvas.drawLines(mPoints, mPaint);
        canvas.restore();
    }

}
