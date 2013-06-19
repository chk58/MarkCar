package com.marker.markcar.map.item;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class ParkingSpace extends MapItem implements Crossable, Selectable {
    public static final float WIDTH = 200;
    public static final float HEIGHT = 400;
    public static final float NAME_HEIGHT = 50;
    public static final int NAME_SIZE = 48;

    private static Bitmap sIcon;
    private final RectF mIconBounds;

    private final String mName;
    private final float mNameX;
    private final float mNameY;

    public ParkingSpace(float x, float y, int degree, String name) {
        sIcon = Icons.sParkIcon;

        mName = name;
        mBounds = new RectF(x, y, x + WIDTH, y + HEIGHT);
        mDegree = degree;

        mPaint = new Paint();
        mPaint.setStrokeWidth(0);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Style.STROKE);
        mPaint.setTextSize(NAME_SIZE);

        float iconL = x + (WIDTH - sIcon.getWidth()) / 2;
        float iconT = y + (HEIGHT - sIcon.getHeight()) / 2;
        float iconR = iconL + sIcon.getWidth();
        float iconB = iconT + sIcon.getHeight();
        mIconBounds = new RectF(iconL, iconT, iconR, iconB);

        mNameX = x + (WIDTH - mPaint.measureText(mName)) / 2;
        mNameY = y + HEIGHT - NAME_HEIGHT;

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate(mDegree, mBounds.left, mBounds.top);
        canvas.drawRect(mBounds, mPaint);
        canvas.drawBitmap(sIcon, null, mIconBounds, mPaint);
        canvas.drawText(mName, mNameX, mNameY, mPaint);
        canvas.restore();
    }
}
