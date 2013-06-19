/*
 * dp = (int) (px / density + 0.5f) 640 * 360
 * px = (int) (dp * density + 0.5f) 1920 * 1080
 */

package com.marker.markcar.map;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

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
import com.marker.markcar.map.item.MapItem;
import com.marker.markcar.map.item.ParkingSpace;
import com.marker.markcar.map.parse.XMLMapParser;

public class MapView extends SurfaceView implements SurfaceHolder.Callback, GestureListener {

    private final static String LOG_TAG = "MapView";
    private final static String THREAD_NAME = "DrawThread";
    private final static int BACKGROUND_COLOR = 0xFFFFFFFF;
    private final static float MAX_SCALE = 1.0f;
    private final static float INIT_SCALE = 0.2f;

    private final static int MAIN_WHAT_MAX_SCALE = 1;

    private final static int THREAD_WHAT_INIT_MAP = 0;
    private final static int THREAD_WHAT_SCALE = 1;
    private final static int THREAD_WHAT_MOVE = 2;

    private SurfaceHolder mHolder;
    private HandlerThread mDrawThread;
    private Handler mThreadHandler;
    private Handler mMainHandler;
    private GestureController mGestureController;
    private Matrix mMatrix;
    private float[] mMatrixValues = new float[9];

    private Map mMap;

    private static class ThreadHandler extends Handler {
        private final SoftReference<MapView> mMap;

        public ThreadHandler(MapView map, Looper looper) {
            super(looper);
            mMap = new SoftReference<MapView>(map);
        }

        @Override
        public void handleMessage(Message msg) {
            MapView map = mMap.get();
            if (map == null) {
                return;
            }
            float[] f;
            switch (msg.what) {
                case THREAD_WHAT_INIT_MAP:
                    map.doInitMap();
                    break;
                case THREAD_WHAT_SCALE:
                    f = (float[]) msg.obj;
                    if (f != null) {
                        map.doScale(f[0], f[1], f[2]);
                    }
                    break;
                case THREAD_WHAT_MOVE:
                    f = (float[]) msg.obj;
                    if (f != null) {
                        map.doMove(f[0], f[1]);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static class MainHandler extends Handler {
        private final SoftReference<MapView> mMap;
        private Toast mToast;
        public MainHandler(MapView map) {
            super(Looper.getMainLooper());
            mMap = new SoftReference<MapView>(map);
        }

        @Override
        public void handleMessage(Message msg) {
            MapView map = mMap.get();
            if (map != null) {
                switch (msg.what) {
                    case MAIN_WHAT_MAX_SCALE:
                        if (mToast != null) {
                            mToast.cancel();
                        }
                        mToast = Toast.makeText(map.getContext(), "Max scale", Toast.LENGTH_SHORT);
                        mToast.show();
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

        ArrayList<MapItem> itemList = new ArrayList<MapItem>();
        for (int i = 0; i < 20; i++) {
            itemList.add(new ParkingSpace(100 + ParkingSpace.WIDTH * i, 100, 0, "A" + (i + 1)));
        }
        mMap = (new XMLMapParser(getContext(), "map_01.xml")).parse();
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
        canvas.setMatrix(mMatrix);
        drawMap(canvas);
        mHolder.unlockCanvasAndPost(canvas);
    }

    private void doMove(float dx, float dy) {
        if (Thread.currentThread() != mDrawThread) {
            throw new RuntimeException("Wrong thread when draw SurfaceView!");
        }

        mMatrix.getValues(mMatrixValues);
        mMatrix.preTranslate(dx / mMatrixValues[Matrix.MSCALE_X], dy / mMatrixValues[Matrix.MSCALE_Y]);

        Canvas canvas = mHolder.lockCanvas();
        canvas.setMatrix(mMatrix);
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
        canvas.setMatrix(mMatrix);
        drawMap(canvas);
        mHolder.unlockCanvasAndPost(canvas);
    }

    private void drawMap(Canvas canvas) {

        clearCanvas(canvas);
        mMap.draw(canvas);

        // Paint p = new Paint();
        // p.setStrokeWidth(0);
        // p.setColor(Color.RED);
        // p.setStyle(Style.STROKE);
        //
        // Rect r = new Rect(100, 100, 200, 200);
        // canvas.drawRect(r, p);
        // canvas.save();
        // // canvas.translate(-100, -100);
        // canvas.scale(4, 4, 100, 100);
        // // canvas.translate(100, 100);
        // // canvas.scale(2, 2);
        // // canvas.translate(-100, -100);
        //
        // canvas.drawRect(r, p);
        // canvas.restore();
        //
        // canvas.save();
        // canvas.scale(0.5f, 0.5f, 500, 500);
        // canvas.scale(4, 4, 100, 100);
        // // canvas.translate(200, 200);
        // p.setColor(Color.BLUE);
        // canvas.drawRect(r, p);
        // canvas.restore();
        //
        // // canvas.scale(0.5f, 0.5f, 200, 200);
        // canvas.scale(2f, 2f, -100, -100);
        // // canvas.translate(200, 200);
        // p.setColor(Color.GRAY);
        // canvas.drawRect(r, p);

        // canvas.drawLine(0, 0, 800, 800, p);
        //
        // // AngleRect ar = new AngleRect(100, 100, 500, 300, 0);
        // // ar.draw(canvas, p);
        // // ar = new AngleRect(100, 100, 100, 200, 30);
        // // p.setColor(Color.GREEN);
        // // ar.draw(canvas, p);
        //
        // Rect r = new Rect(300, 300, 400, 400);
        // canvas.rotate(30, 300, 300);
        // canvas.drawRect(r, p);
        // canvas.rotate(-30, 300, 300);
        //
        // r = new Rect(600, 600, 700, 700);
        // p.setColor(Color.BLUE);
        // canvas.drawRect(r, p);
        //
        // r = new Rect(0, 0, 10, 10);
        // p.setColor(Color.GREEN);
        // // canvas.drawRect(r, p);
        //
        // canvas.drawLine(100, 0, 100, 1000, p);
        // // canvas.translate(100, 0);
        //
        // r = new Rect(0, 0, 100, 100);
        // canvas.drawRect(r, p);
        //
        // r = new Rect(100, 100, 200, 200);
        // canvas.translate(100, 0);
        // canvas.scale(2, 2, 0, 0);
        // canvas.translate(-100, 0);
        // canvas.drawRect(r, p);

        // Path path = new Path();
        // path.moveTo(0, 0);
        // path.lineTo(100, 100);
        //
        // path.lineTo(30, 70);
        // path.close();
        // canvas.drawRect(300, 0, 400, 100, p);
        //
        // // p.setColor(Color.BLACK);
        // canvas.drawPath(path, p);
        // canvas.scale(1, 1);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mDrawThread = new HandlerThread(THREAD_NAME);
        mDrawThread.start();
        mThreadHandler = new ThreadHandler(this, mDrawThread.getLooper());
        mThreadHandler.sendEmptyMessage(THREAD_WHAT_INIT_MAP);
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

    @Override
    public boolean onClick(MotionEvent e) {
        String s = "RawX:" + e.getRawX() + " RawY:" + e.getRawY() + " x:" + e.getX() + " x:" + e.getY();
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
        return true;
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
}
