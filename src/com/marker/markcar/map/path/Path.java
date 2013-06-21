package com.marker.markcar.map.path;

import java.util.ArrayList;

import com.marker.markcar.map.item.MapItem;

public class Path {
    private MapItem mStartItem;
    private MapItem mEndItem;
    private final ArrayList<PathPoint> mOpenList;
    private final ArrayList<PathPoint> mCloseList;

    public Path(MapItem start, MapItem end) {
        mStartItem = start;
        mEndItem = end;
        mOpenList = new ArrayList<PathPoint>();
        mCloseList = new ArrayList<PathPoint>();
    }

    public void reset(MapItem start, MapItem end) {
        mStartItem = start;
        mEndItem = end;
        mOpenList.clear();
        mCloseList.clear();
    }
}
