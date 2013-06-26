/*
 * dp = (int) (px / density + 0.5f) 640 * 360
 * px = (int) (dp * density + 0.5f) 1920 * 1080
 */

package com.marker.markcar.map;

import java.lang.ref.SoftReference;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.marker.markcar.map.GestureController.GestureListener;
import com.marker.markcar.map.item.Icons;
import com.marker.markcar.map.item.Map;
import com.marker.markcar.map.item.SelectableItem;
import com.marker.markcar.map.item.SelectableItem.OnPressedListener;

public class MapView extends SurfaceView implements SurfaceHolder.Callback, GestureListener {

    public final static String LOG_TAG = "MapView";
    private final static String THREAD_NAME = "DrawThread";
    private final static int BACKGROUND_COLOR = 0xFFFFFFFF;
    private final static float MAX_SCALE = 1.0f;
    private final static float INIT_SCALE = 0.2f;

    private final static int MAIN_WHAT_MAX_SCALE = 0;
    private final static int MAIN_WHAT_ITEM_PRESSED = 1;

    private final static int THREAD_WHAT_INIT_MAP = 0;
    private final static int THREAD_WHAT_SCALE = 1;
    private final static int THREAD_WHAT_MOVE = 2;
    private final static int THREAD_WHAT_CLICK = 3;
    private final static int THREAD_WHAT_REFRESH = 4;

    private SurfaceHolder mHolder;
    private HandlerThread mDrawThread;
    private Handler mThreadHandler;
    private Handler mMainHandler;
    private GestureController mGestureController;
    private Matrix mMatrix;
    private float[] mMatrixValues = new float[9];
    private Map mMap;
    private OnPressedListener mOnPressedListener;

    private static class ThreadHandler extends Handler {
        private final SoftReference<MapView> mMapView;

        public ThreadHandler(MapView map, Looper looper) {
            super(looper);
            mMapView = new SoftReference<MapView>(map);
        }

        @Override
        public void handleMessage(Message msg) {
            MapView view = mMapView.get();
            if (view == null) {
                return;
            }
            float[] f;
            switch (msg.what) {
                case THREAD_WHAT_INIT_MAP:
                    view.doInitMap();
                    break;
                case THREAD_WHAT_SCALE:
                    f = (float[]) msg.obj;
                    if (f != null) {
                        view.doScale(f[0], f[1], f[2]);
                    }
                    break;
                case THREAD_WHAT_MOVE:
                    f = (float[]) msg.obj;
                    if (f != null) {
                        view.doMove(f[0], f[1]);
                    }
                    break;
                case THREAD_WHAT_CLICK:
                    f = (float[]) msg.obj;
                    if (f != null) {
                        view.doClick(f[0], f[1]);
                    }
                    break;
                case THREAD_WHAT_REFRESH:
                    view.doRefresh();
                    break;
                default:
                    break;
            }
        }
    }

    private static class MainHandler extends Handler {
        private final SoftReference<MapView> mMapView;
        private Toast mToast;
        public MainHandler(MapView map) {
            super(Looper.getMainLooper());
            mMapView = new SoftReference<MapView>(map);
        }

        @Override
        public void handleMessage(Message msg) {
            MapView map = mMapView.get();
            if (map != null) {
                switch (msg.what) {
                    case MAIN_WHAT_MAX_SCALE:
                        if (mToast != null) {
                            mToast.cancel();
                        }
                        mToast = Toast.makeText(map.getContext(), "Max scale", Toast.LENGTH_SHORT);
                        mToast.show();
                        break;
                    case MAIN_WHAT_ITEM_PRESSED:
                        SelectableItem item = (SelectableItem) msg.obj;
                        if (map.mOnPressedListener != null && item != null) {
                            map.mOnPressedListener.OnPressed(item);
                        }
                        break;
                }
            }
        }
    }

    public MapView(Context context) {
        super(context);
        init();
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Icons.init(getContext());
        mHolder = getHolder();
        mHolder.addCallback(this);
        mGestureController = new GestureController(getContext(), this);
        mMainHandler = new MainHandler(this);
    }

    public void setMap(Map map) {
        mMap = map;
        if (mDrawThread != null && mDrawThread.isAlive() && mThreadHandler != null) {
            mThreadHandler.sendEmptyMessage(THREAD_WHAT_INIT_MAP);
        }
    }

    private void clearCanvas(Canvas canvas) {
        canvas.drawColor(BACKGROUND_COLOR);
    }

    private void doInitMap() {
        if (Thread.currentThread() != mDrawThread) {
            throw new RuntimeException("Wrong thread when draw SurfaceView!");
        }

        if (mMatrix == null) {
            mMatrix = new Matrix();
        }
        mMatrix.reset();
        mMatrix.setScale(INIT_SCALE, INIT_SCALE);

        Canvas canvas = mHolder.lockCanvas();
        drawMap(canvas);
        mHolder.unlockCanvasAndPost(canvas);
    }

    private void doClick(float x, float y) {
        if (Thread.currentThread() != mDrawThread) {
            throw new RuntimeException("Wrong thread when draw SurfaceView!");
        }
//
//        boolean needRedraw = false;
//        boolean cancelSelect = false;
//        SelectableItem temp = null;
        mMatrix.getValues(mMatrixValues);
        float sx = (x - mMatrixValues[Matrix.MTRANS_X]) / mMatrixValues[Matrix.MSCALE_X];
        float sy = (y - mMatrixValues[Matrix.MTRANS_Y]) / mMatrixValues[Matrix.MSCALE_Y];

        for (SelectableItem item : mMap.getSelectableList()) {
            if (item.contains(sx, sy)) {
                mMainHandler.obtainMessage(MAIN_WHAT_ITEM_PRESSED, item).sendToTarget();
                break;
            }
        }
//        if (needRedraw) {
//            if (!cancelSelect) {
//                for (SelectableItem item : mMap.getSelectableList()) {
//                    if (item.isSelected() && item != temp) {
//                        item.setSelected(false);
//                        break;
//                    }
//                }
//            }
//
//            Canvas canvas = mHolder.lockCanvas();
//            drawMap(canvas);
//            mHolder.unlockCanvasAndPost(canvas);
//        }
    }

    private void doMove(float dx, float dy) {
        if (Thread.currentThread() != mDrawThread) {
            throw new RuntimeException("Wrong thread when draw SurfaceView!");
        }

        mMatrix.getValues(mMatrixValues);

        float dxs = dx / mMatrixValues[Matrix.MSCALE_X];
        float dys = dy / mMatrixValues[Matrix.MSCALE_Y];

        float x = mMatrixValues[Matrix.MTRANS_X];
        float y = mMatrixValues[Matrix.MTRANS_Y];
        float s = mMatrixValues[Matrix.MSCALE_X];

        float maxX = getWidth() - mMap.getWidth() * s;
        float maxY = getHeight() - mMap.getHeight() * s;

        if (dxs + x > 0) {
            dxs = -x;
        } else if (dxs + x < maxX) {
            dxs = maxX - x;
        }
        if (dys + y > 0) {
            dys = -y;
        } else if (dys + y < maxY) {
            dys = maxY - y;
        }
        mMatrix.preTranslate(dxs, dys);

        Canvas canvas = mHolder.lockCanvas();
        drawMap(canvas);
        mHolder.unlockCanvasAndPost(canvas);
    }

    private void doScale(float scale, float scaleX, float scaleY) {
        if (Thread.currentThread() != mDrawThread) {
            throw new RuntimeException("Wrong thread when draw SurfaceView!");
        }
        if (scale <= 0 || scaleX < 0 || scaleY < 0) {
            throw new IllegalArgumentException();
        }

        mMatrix.getValues(mMatrixValues);
        float s = scale;
        if (s * mMatrixValues[Matrix.MSCALE_X] >= MAX_SCALE) {
            s = MAX_SCALE / mMatrixValues[Matrix.MSCALE_X];
            mMainHandler.obtainMessage(MAIN_WHAT_MAX_SCALE).sendToTarget();
        }
        mMatrix.postScale(s, s, scaleX, scaleY);

        Canvas canvas = mHolder.lockCanvas();
        drawMap(canvas);
        mHolder.unlockCanvasAndPost(canvas);
    }

    private void doRefresh() {
        if (Thread.currentThread() != mDrawThread) {
            throw new RuntimeException("Wrong thread when draw SurfaceView!");
        }

        Canvas canvas = mHolder.lockCanvas();
        drawMap(canvas);
        mHolder.unlockCanvasAndPost(canvas);
    }

    private void drawMap(Canvas canvas) {
        if (canvas != null) {
            canvas.setMatrix(mMatrix);
            clearCanvas(canvas);
            mMap.draw(canvas);
        }
    }

    public void refreshMap() {
        if (mMap != null && mDrawThread != null && mDrawThread.isAlive() && mThreadHandler != null) {
            mThreadHandler.sendEmptyMessage(THREAD_WHAT_REFRESH);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mDrawThread = new HandlerThread(THREAD_NAME);
        mDrawThread.start();
        mThreadHandler = new ThreadHandler(this, mDrawThread.getLooper());

        if (mMap != null) {
            mThreadHandler.sendEmptyMessage(THREAD_WHAT_INIT_MAP);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mDrawThread != null) {
            mDrawThread.quit();
            mDrawThread = null;
        }
        mThreadHandler = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureController.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private float[] mClickInfo;

    @Override
    public boolean onClick(MotionEvent e) {
        if (mThreadHandler != null) {
            if (mClickInfo == null) {
                mClickInfo = new float[2];
            }
            mClickInfo[0] = e.getX();
            mClickInfo[1] = e.getY();
            mThreadHandler.obtainMessage(THREAD_WHAT_CLICK, mClickInfo).sendToTarget();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onDoubleClick(MotionEvent e) {
        if (mThreadHandler != null) {
            mThreadHandler.sendEmptyMessage(THREAD_WHAT_INIT_MAP);
            return true;
        } else {
            return false;
        }
    }

    private float[] mScaleInfo;

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        if (mThreadHandler != null) {
            if (mScaleInfo == null) {
                mScaleInfo = new float[3];
            }
            mScaleInfo[0] = detector.getScaleFactor();
            mScaleInfo[1] = detector.getFocusX();
            mScaleInfo[2] = detector.getFocusY();
            mThreadHandler.obtainMessage(THREAD_WHAT_SCALE, mScaleInfo).sendToTarget();
            return true;
        } else {
            return false;
        }
    }

    private float[] mMoveInfo;

    @Override
    public boolean onMove(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mThreadHandler != null) {
            if (mMoveInfo == null) {
                mMoveInfo = new float[2];
            }
            mMoveInfo[0] = -distanceX;
            mMoveInfo[1] = -distanceY;
            mThreadHandler.obtainMessage(THREAD_WHAT_MOVE, mMoveInfo).sendToTarget();
            return true;
        } else {
            return false;
        }
    }

    public void setOnPressedListener(OnPressedListener listener) {
        this.mOnPressedListener = listener;
    }
}
