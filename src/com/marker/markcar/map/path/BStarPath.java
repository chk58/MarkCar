package com.marker.markcar.map.path;

import java.util.ArrayList;

import android.util.Log;

import com.marker.markcar.map.item.Crossable;
import com.marker.markcar.map.item.MapItem;

public class BStarPath {
    private static float COST_RATE_STRAIGHT = 1;
    private static float COST_RATE_OBLIQUE = 1.4f;

    private static float MOVE_UNIT = 100;

    private MapItem mStartItem;
    private MapItem mEndItem;
    private BStarPathPoint mStartPoint;
    private BStarPathPoint mEndPoint;
    // private BStarPathPoint mCurrentPoint;
    private final ArrayList<MapItem> mItemList;
//    private final ArrayList<BStarPathPoint> mOpenList = new ArrayList<BStarPathPoint>();
//    private final ArrayList<BStarPathPoint> mCloseList = new ArrayList<BStarPathPoint>();
    private final ArrayList<ArrayList<BStarPathPoint>> mBranchList = new ArrayList<ArrayList<BStarPathPoint>>();
    //private final ArrayList<BStarPathPoint> mFreeList = new ArrayList<BStarPathPoint>();
    private boolean mComplete;

    public BStarPath(MapItem start, MapItem end, ArrayList<MapItem> itemList) {
        reset(start, end);
        mItemList = itemList;
    }

    public void reset(MapItem start, MapItem end) {
        mStartItem = start;
        mEndItem = end;
        mComplete = false;
    }

    public boolean isComplete() {
        return mComplete;
    }

    public void computePath() {
        mBranchList.clear();
        mStartPoint = new BStarPathPoint(mStartItem.getCenterX(), mStartItem.getCenterY());
        mEndPoint = new BStarPathPoint(mEndItem.getCenterX(), mEndItem.getCenterY());
        // mCloseList.add(mStartPoint);
        // mCloseList.add(mEndPoint);

        ArrayList<BStarPathPoint> startBranch = new ArrayList<BStarPathPoint>();
        startBranch.add(mStartPoint);
        mBranchList.add(startBranch);
        //mFreeList.add(mStartPoint);
        // mBranchList.add(startBranch);

        // mCurrentPoint = mStartPoint;
        int count = 0;
        while (!mComplete) {
            count++;
            for (ArrayList<BStarPathPoint> branch : mBranchList) {
                BStarPathPoint bpp = branch.get(branch.size() - 1);
                BStarPathPoint next = bpp.getNext(mEndPoint);
                if (mEndItem.contains(next.x, next.y)) {
                    mComplete = true;
                    break;
                } else if (crossAble(next)) {;
                    next.setFather(bpp);
                    branch.add(next);
                } else {
                    float[] branchPoints = bpp.getBranchXYs(mEndPoint);

                    // ArrayList<BStarPathPoint> branch = new
                    // ArrayList<BStarPathPoint>();
                }
            }
        }
        Log.d("chk", "count : " + count);
        // Log.d("chk", "mOpenList : " + mOpenList.size());
        // Log.d("chk", "mCloseList : " + mCloseList.size());
    }

    private boolean crossAble(BStarPathPoint bpp) {
        boolean result = true;
        for (MapItem item : mItemList) {
            if (!(item instanceof Crossable) && item.contains(bpp.x, bpp.y)) {
                result = false;
            }
        }
        return result;
    }

}
