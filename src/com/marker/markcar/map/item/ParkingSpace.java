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
    public static final int NAME_SIZE_SELECTED = 56;

    private static Bitmap sIcon;
    private final RectF mIconBounds;

    private final String mName;
    private final float mNameX;
    private final float mNameY;
    private final float mNameXSelected;
    private final float mNameYSelected;

    private boolean mIsSelected = false;

    public ParkingSpace(float x, float y, int degree, String name) {
        sIcon = Icons.sParkIcon;

        mName = name;
        mBounds = new RectF(x, y, x + WIDTH, y + HEIGHT);
        mDegree = degree;

        mPaint = new Paint();
        mPaint.setStrokeWidth(0);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Style.STROKE);

        float iconL = x + (WIDTH - sIcon.getWidth()) / 2;
        float iconT = y + (HEIGHT - sIcon.getHeight()) / 2;
        float iconR = iconL + sIcon.getWidth();
        float iconB = iconT + sIcon.getHeight();
        mIconBounds = new RectF(iconL, iconT, iconR, iconB);

        mPaint.setTextSize(NAME_SIZE_SELECTED);
        mNameXSelected = x + (WIDTH - mPaint.measureText(mName)) / 2;
        mNameYSelected = y + HEIGHT - NAME_HEIGHT;

        mPaint.setTextSize(NAME_SIZE);
        mNameX = x + (WIDTH - mPaint.measureText(mName)) / 2;
        mNameY = y + HEIGHT - NAME_HEIGHT;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate(mDegree, mBounds.left, mBounds.top);

        float nameX;
        float nameY;
        if (mIsSelected) {
            mPaint.setTextSize(NAME_SIZE_SELECTED);
            nameX = mNameXSelected;
            nameY = mNameYSelected;
        } else {
            mPaint.setTextSize(NAME_SIZE);
            nameX = mNameX;
            nameY = mNameY;
        }
        canvas.drawRect(mBounds, mPaint);
        canvas.drawBitmap(sIcon, null, mIconBounds, mPaint);
        canvas.drawText(mName, nameX, nameY, mPaint);

        canvas.restore();
    }

    @Override
    public boolean isSelected() {
        return mIsSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    @Override
    public boolean contains(float x, float y) {
        boolean result = false;

        return result;
    }
}
