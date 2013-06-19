package com.marker.markcar.map;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

public class GestureController extends SimpleOnGestureListener implements OnScaleGestureListener {

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGenericDetector;
    private GestureListener mListener;
    private boolean mMultiPoint = false;

    public interface GestureListener {
        public boolean onClick(MotionEvent e);

        public boolean onDoubleClick(MotionEvent e);

        public boolean onScale(ScaleGestureDetector detector);

        public boolean onMove(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);
    }

    public GestureController(Context context, GestureListener listener) {
        mScaleDetector = new ScaleGestureDetector(context, this);
        mGenericDetector = new GestureDetector(context, this);
        mListener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                mMultiPoint = true;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() < 3) {
                    mMultiPoint = false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mMultiPoint = false;
                break;
        }
        if (mGenericDetector.onTouchEvent(event)) {
            result = true;
        }
        if (mScaleDetector.onTouchEvent(event)) {
            result = true;
        }
        return result;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mMultiPoint) {
            return false;
        }
        return mListener.onMove(e1, e2, distanceX, distanceY);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (mMultiPoint) {
            return false;
        }
        return mListener.onDoubleClick(e);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (mMultiPoint) {
            return false;
        }
        return mListener.onClick(e);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (!mMultiPoint) {
            return false;
        }
        return mListener.onScale(detector);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        if (!mMultiPoint) {
            return false;
        }
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }
}
