package com.marker.markcar.map.path;

public class PathPoint implements Comparable<PathPoint> {
    private PathPoint mFather;
    final float x;
    final float y;
    private float G;
    private final float H;

    public PathPoint(float x, float y, float h) {
        this.x = x;
        this.y = y;
        this.H = h;
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

    public PathPoint getFather() {
        return mFather;
    }

    public void setFather(PathPoint father) {
        this.mFather = father;
    }

    public float getG() {
        return G;
    }

    public void setG(float g) {
        G = g;
    }

    public float getH() {
        return H;
    }
    
    public float getF() {
        return G + H;
    }

    public boolean equals(float x, float y) {
        return (this.x == x && this.y == y);
    }

    @Override
    public int compareTo(PathPoint another) {
        if (this.getF() > another.getF()) {
            return 1;
        } else if (this.getF() == another.getF()) {
            return -1;
        } else {
            return 0;
        }
    }
}
