package com.marker.markcar.map.path;

public class BStarPathPoint implements Comparable<BStarPathPoint> {
    private static float MOVE_UNIT = 100;
    private BStarPathPoint mFather;
    private int mBranchOrientation;

    final float x;
    final float y;


    public BStarPathPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public BStarPathPoint getNext(BStarPathPoint end) {
        float x;
        float y;
        if (Math.abs(end.y - this.y) > Math.abs(end.x - this.x)) {
            x = this.x;
            y = (end.y > this.y) ? this.y + MOVE_UNIT : this.y - MOVE_UNIT;
        } else {
            x = (end.x > this.x) ? this.x + MOVE_UNIT : this.x - MOVE_UNIT;
            y = this.y;
        }

        return new BStarPathPoint(x, y);
    }

    public float[] getBranchXYs(BStarPathPoint end) {
        float x1;
        float y1;
        float x2;
        float y2;

        if (Math.abs(end.y - this.y) > Math.abs(end.x - this.x)) {
            x1 = this.x + MOVE_UNIT;
            y1 = this.y;
            x2 = this.x - MOVE_UNIT;
            y2 = this.y;
        } else {
            x1 = this.x;
            y1 = this.y + MOVE_UNIT;
            x2 = this.x;
            y2 = this.y - MOVE_UNIT;
        }
        return new float[] {x1, y1, x2, y2};
    }

    public float[][] getNearXYs() {
        float[][] result = new float[8][2];
        // float x = x - u;
        // float y = y - u;
        //
        // x = x;
        // y = y - u;
        //
        // // x = x + u;
        // // y = y - u;
        //
        // x = x - u;
        // y = y;
        //
        // x = x + u;
        // y = y;
        //
        // // x = x - u;
        // // y = y + u;
        //
        // x = x;
        // y = y + u;
        //
        // // x = x + u;
        // // y = y + u;
        //
        return result;
    }

    public BStarPathPoint getFather() {
        return mFather;
    }

    public void setFather(BStarPathPoint father) {
        this.mFather = father;
    }

    public boolean equals(float x, float y) {
        return (this.x == x && this.y == y);
    }

    @Override
    public int compareTo(BStarPathPoint another) {
        // if (this.getF() > another.getF()) {
        // return 1;
        // } else if (this.getF() == another.getF()) {
        // return -1;
        // }
        return 0;
    }

}
