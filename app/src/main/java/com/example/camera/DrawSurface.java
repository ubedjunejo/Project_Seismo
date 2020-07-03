package com.example.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class DrawSurface extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = DrawSurface.class.getSimpleName();

    private DrawSurfaceCallback onReadyCallback;
    private boolean interactive = false;

    private SurfaceHolder holder = null;

    private final Context context;

    public Paint paintGreen = null;
    public Paint paintRed = null;

    private float angle = (float) Math.toRadians(10d);

    private float length;
    private float x1, y1;
    private float x2, y2;
    float a, b, c, d;

    public DrawSurface(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DrawSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public DrawSurface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init() {
        holder = getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSPARENT);

        this.setZOrderOnTop(true);
        this.setWillNotDraw(false);

        paintGreen = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintGreen.setColor(Color.GREEN);
        paintGreen.setStyle(Paint.Style.STROKE);
        paintGreen.setStrokeWidth(10.0f);

        paintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRed.setColor(Color.GREEN);
        paintRed.setStyle(Paint.Style.STROKE);
        paintRed.setStrokeWidth(paintGreen.getStrokeWidth());

        //https://stackoverflow.com/questions/1016896/how-to-get-screen-dimensions-as-pixels-in-android/1016941#1016941
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float width = size.x;
        float height = size.y;
        length=width/2;
        x1=length-(width/4); x2=length+(width/4);
        y1=height/2; y2=height/2;
    }

    public void setCallback(DrawSurfaceCallback onReadyCallback) {
        this.onReadyCallback = onReadyCallback;

        if (holder.getSurface().isValid()) onReadyCallback.onSurfaceReady(this);
    }

    public void setInteractive(boolean interative) {
        this.interactive = interative;
    }

    public void clearCanvas() throws Exception {
        if (!holder.getSurface().isValid()) {
            throw new Exception("surface not valid");
        }

        Canvas canvas = holder.lockCanvas();

        if (canvas == null) {
            throw new Exception("canvas not valid");
        }

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        holder.unlockCanvasAndPost(canvas);
    }

    public void setAngle(float theta) {
        a=(float)(540+((x1-540)*Math.cos(theta)+(1002-y1)*Math.sin(theta)));
        b=(float)(540+((x2-540)*Math.cos(theta)+(1002-y2)*Math.sin(theta)));

        c=(float)(1002-((1002-y1)*Math.cos(theta)-(x1-540)*Math.sin(theta)));
        d=(float)(1002-((1002-y2)*Math.cos(theta)-(x2-540)*Math.sin(theta)));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(a, c, b, d, new Paint());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (onReadyCallback != null) {
            onReadyCallback.onSurfaceReady(this);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public static class DrawSurfaceCallback {

        public DrawSurfaceCallback() {
        }

        public void onSurfaceReady(DrawSurface surface) {

        }
    }
}
