package com.example.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

//Based on the sample provided by the tutor: Chritopher J. Getschmann

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class DrawSurface extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = DrawSurface.class.getSimpleName();

    private DrawSurfaceCallback onReadyCallback;
    private boolean interactive = false;

    private SurfaceHolder holder = null;

    private final Context context;

    public Paint paintGreen = null;
    public Paint paintBlack = null;

    private float length;
    long t0; float t;//x-axis time value t
    float width, height;//screen parameters
    float previous=0;//storage for old x-axis value
    double n=0;

    float acceleration;//acceleration value
    double beat;//light intensity value
    Path path;//path for plotting accelerometer data
    Path path2;//path for ppg plot

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

    public void init() {
        holder = getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSPARENT);

        this.setZOrderOnTop(true);
        this.setWillNotDraw(false);

        paintGreen = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintGreen.setColor(Color.GREEN);
        paintGreen.setStyle(Paint.Style.STROKE);
        paintGreen.setStrokeWidth(2.0f);

        paintBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBlack.setColor(Color.BLACK);
        paintBlack.setStyle(Paint.Style.STROKE);
        paintBlack.setStrokeWidth(paintGreen.getStrokeWidth());

        //https://stackoverflow.com/questions/1016896/how-to-get-screen-dimensions-as-pixels-in-android/1016941#1016941
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        length=width/2;

        //intialize plots; get current time for calculating updates
        path=new Path();
        path2=new Path();
        t0=System.currentTimeMillis();
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

    public void setAcceleration(float val) {
        acceleration=1000*val;//scale
        invalidate();
    }

    public void setBeat(double beat){
        this.beat=5*beat;//scale
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        n++;
        super.onDraw(canvas);
        float maxX=width;
        float minYa= (float) (height/1.5);//accelerometer min
        float minYb=height/3;//beat min
        //Log.w(TAG, "onDraw: "+(minY+c)+ " : "+(minY+d) );

        t=(System.currentTimeMillis()-t0)/50;//this equals around 1 pix; optimal value found by trial & error instead of ab-initio

        if (t>=maxX){//move plot to left when it reaches the end of the screen on the right side
            path.offset((float) (-(t-previous)),0);
            path2.offset((float) (-(t-previous)), 0);
        }
        previous=t;//update previous value for next offset operation

        path.lineTo(t-10, minYa+acceleration);//-10 to drop initial noise
        canvas.drawPath(path, paintGreen);

        path2.lineTo(t-10, (float) (minYb-beat));//invert y
        canvas.drawPath(path2, paintBlack);
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
