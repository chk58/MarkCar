package com.marker.markcar.map.path;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.marker.markcar.map.item.Crossable;
import com.marker.markcar.map.item.MapItem;

public class Path {
    private static float COST_RATE_STRAIGHT = 1;
    private static float COST_RATE_OBLIQUE = 1.4f;

    private static float MOVE_UNIT = 100;

    private MapItem mStartItem;
    private MapItem mEndItem;
    private PathPoint mStartPoint;
    private PathPoint mEndPoint;
    private PathPoint mCurrentPoint;
    private final ArrayList<MapItem> mItemList;
    private final ArrayList<PathPoint> mOpenList = new ArrayList<PathPoint>();
    private final ArrayList<PathPoint> mCloseList = new ArrayList<PathPoint>();
    private boolean mComplete;

    private final ArrayList<PathPoint> result = new ArrayList<PathPoint>();
    public Path(MapItem start, MapItem end, ArrayList<MapItem> itemList) {
        reset(start, end);
        mItemList = itemList;
    }

    public void reset(MapItem start, MapItem end) {
        mStartItem = start;
        mEndItem = end;
        mComplete = false;
    }

    public void computePath() {
        mOpenList.clear();
        mCloseList.clear();
        mStartPoint = new PathPoint(mStartItem.getCenterX(), mStartItem.getCenterY(), 0);
        mEndPoint = new PathPoint(mEndItem.getCenterX(), mEndItem.getCenterY(), 0);
        mCloseList.add(mStartPoint);
        mCloseList.add(mEndPoint);
        mCurrentPoint = mStartPoint;
        int count = 0;
        while (!mComplete) {
            count++;
            addOpenPoints();
            closeNextPoint();
            if (mEndItem.contains(mCurrentPoint.x, mCurrentPoint.y)) {
                mComplete = true;
            }
        }
        Log.d("chk", "count : " + count);
        Log.d("chk", "mOpenList : " + mOpenList.size());
        Log.d("chk", "mCloseList : " + mCloseList.size());
    }

    public boolean isComplete() {
        return mComplete;
    }

    private void closeNextPoint() {
        PathPoint pp = mOpenList.get(mOpenList.size() - 1);
        for (int i = mOpenList.size() - 2; i >= 0; i--) {
            PathPoint ppi = mOpenList.get(i);
            if (pp.compareTo(ppi) > 0) {
                pp = ppi;
            }
        }
        mCurrentPoint = pp;
        mOpenList.remove(pp);
        mCloseList.add(pp);
    }

    private void addOpenPoint(float x, float y) {
        if (!existInList(x, y, mCloseList) && crossAble(x, y)) {
            PathPoint pp = getPointByXY(x, y, mOpenList);
            if (pp != null) {
                float newG = computG(pp, mCurrentPoint);
                if (newG < pp.getG()) {
                    pp.setG(newG);
                    pp.setFather(mCurrentPoint);
                }
            } else {
                float h = COST_RATE_STRAIGHT * (Math.abs(x - mEndPoint.x) + Math.abs(y - mEndPoint.y));
                pp = new PathPoint(x, y, h);
                pp.setFather(mCurrentPoint);
                pp.setG(computG(pp, pp.getFather()));
                mOpenList.add(pp);
            }
        }
    }

    private void addOpenPoints() {
        float x = mCurrentPoint.x - MOVE_UNIT;
        float y = mCurrentPoint.y - MOVE_UNIT;
        // addOpenPoint(x, y);

        x = mCurrentPoint.x;
        y = mCurrentPoint.y - MOVE_UNIT;
        addOpenPoint(x, y);

        // x = mCurrentPoint.x + u;
        // y = mCurrentPoint.y - u;
        // addOpenPoint(x, y);

        x = mCurrentPoint.x - MOVE_UNIT;
        y = mCurrentPoint.y;
        addOpenPoint(x, y);

        x = mCurrentPoint.x + MOVE_UNIT;
        y = mCurrentPoint.y;
        addOpenPoint(x, y);

        // x = mCurrentPoint.x - u;
        // y = mCurrentPoint.y + u;
        // addOpenPoint(x, y);

        x = mCurrentPoint.x;
        y = mCurrentPoint.y + MOVE_UNIT;
        addOpenPoint(x, y);

        // x = mCurrentPoint.x + u;
        // y = mCurrentPoint.y + u;
        // addOpenPoint(x, y);
    }

    private PathPoint getPointByXY(float x, float y, ArrayList<PathPoint> list) {
        for (PathPoint pp : list) {
            if (pp.equals(x, y)) {
                return pp;
            }
        }
        return null;
    }

    private float computG(PathPoint pp, PathPoint father) {
        float rate;
        if (father.x != pp.x && father.y != pp.y) {
            rate = COST_RATE_OBLIQUE;
        } else {
            rate = COST_RATE_STRAIGHT;
        }
        rate += getAdditionRate(pp);
        return father.getG() + rate * MOVE_UNIT;
    }

    private float getAdditionRate(PathPoint pp) {
        for (MapItem item : mItemList) {
            if ((item instanceof Crossable) && item.contains(pp.x, pp.y)) {
                return ((Crossable) item).getCostRate();
            }
        }
        return 0;
    }

    private boolean crossAble(float x, float y) {
        boolean result = true;
        for (MapItem item : mItemList) {
            if (!(item instanceof Crossable) && item.contains(x, y)) {
                result = false; 
            }
        }
        return result;
    }

    private boolean existInList(float x, float y, ArrayList<PathPoint> list) {
        for (PathPoint pp : list) {
            if (pp.equals(x, y)) {
                return true;
            }
        }
        return false;
    }

    public void drawPath(Canvas canvas) {
        PathPoint pp = mCurrentPoint;
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        for (PathPoint p : mCloseList) {
            canvas.drawRect(p.x - MOVE_UNIT / 2, p.y - MOVE_UNIT / 2, p.x + MOVE_UNIT / 2, p.y + MOVE_UNIT / 2, paint);
        }
        paint.setColor(Color.RED);
        while (pp.getFather() != null) {
            canvas.drawLine(pp.x, pp.y, pp.getFather().x, pp.getFather().y, paint);
            pp = pp.getFather();
            result.add(pp);
        }
        // int i = 0;
    }
}
