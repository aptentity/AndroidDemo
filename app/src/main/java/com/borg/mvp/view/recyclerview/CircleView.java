package com.borg.mvp.view.recyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by moon.zhong on 2015/7/17.
 * time : 15:54
 */
public class CircleView extends View {

    private final static int RADIUS_DEFAULT = 100;

    private final static int MARGIN_DEFAULT = 10;

    private int mRadius;

    private final Rect mBound = new Rect();
    private final Rect mBoundView = new Rect();


    private Resources mResources;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Path mPath = new Path();
    private Path mCirclePath = new Path();
    private PathMeasure mPathMeasure ;

    private float mPercent;

    public CircleView(Context context) {
        super(context);
        initView();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mResources = getResources();
        final DisplayMetrics metrics = mResources.getDisplayMetrics();
        final float density = metrics.density;
        mRadius = (int) (density * RADIUS_DEFAULT);
        mBound.set(0, 0, mRadius, mRadius);
        mBoundView.set(mBound);
        mBoundView.inset((int) (density * MARGIN_DEFAULT), (int) (density * MARGIN_DEFAULT));
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(density * 3);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPathMeasure = new PathMeasure() ;
//        mPaint.setPathEffect(new CornerPathEffect(mPaint.getStrokeWidth())) ;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.RED);
        makePath();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLUE);
        mPaint.setPathEffect(null) ;
        canvas.drawPath(mPath, mPaint);
        mPaint.setColor(Color.GRAY);
        float length = mPathMeasure.getLength() ;
        PathEffect pathEffect = new DashPathEffect(new float[]{length,length},length*(1 - mPercent)) ;
        mPaint.setPathEffect(pathEffect) ;
        float pot[] = new float[2] ;
        mPathMeasure.getPosTan(length*(1-mPercent),pot,null) ;
        canvas.drawPath(mCirclePath, mPaint);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(null) ;
        canvas.drawCircle(pot[0],pot[1],mPaint.getStrokeWidth(),mPaint);
//        canvas.drawCircle(mBoundView.exactCenterX(), mBoundView.exactCenterY(), mBoundView.width() / 2, mPaint);
    }

    private void makePath() {
        mPath.reset();
        //center point
        float centerX = mBoundView.exactCenterX();
        float centerY = mBoundView.exactCenterY();

        //A point
        double angle = Math.PI / 3.0F;
        float A_pointX = centerX - (float) (Math.sin(angle) * mBoundView.width() / 2.0f);
        float A_pointY = centerY - mBoundView.width() / 4.0f;
        mPath.moveTo(A_pointX, A_pointY);


        float B_pointX = centerX;
        float B_pointY = centerY + mBoundView.width() / 2;

        float length = (float) (mBoundView.width() / 2 / Math.sin(Math.PI * 5 / 18));
        length = length / 2 + mPercent * length / 2 - 8F;
        float X_dis = (float) (length * Math.cos(Math.PI / 18));
        float Y_dis = (float) (length * Math.sin(Math.PI / 18));
        float X_dis_1 = (float) (length * Math.cos(Math.PI * 5 / 18));
        float Y_dis_1 = (float) (length * Math.sin(Math.PI * 5 / 18));
        float C_pointX = centerX + (float) (Math.sin(angle) * mBoundView.width() / 2.0f);
        float C_pointY = A_pointY;
        float A_C_XX = (float) (length * Math.sin(Math.PI * 2 / 18));
        float A_C_YY = (float) (length * Math.cos(Math.PI * 2 / 18));


        mPath.cubicTo(centerX - X_dis, centerY + Y_dis, centerX - X_dis_1, centerY + Y_dis_1, B_pointX, B_pointY);
        mPath.cubicTo(centerX + X_dis_1, centerY + Y_dis_1, centerX + X_dis, centerY + Y_dis, C_pointX, C_pointY);
        mPath.cubicTo(centerX + A_C_XX, centerY - A_C_YY, centerX - A_C_XX, centerY - A_C_YY, A_pointX, A_pointY);
        mPath.close();
        mCirclePath.reset();
        mCirclePath.addCircle(mBoundView.exactCenterX(), mBoundView.exactCenterY(), mBound.width() / 2 - 5, Path.Direction.CCW);
        mPathMeasure.setPath(mPath,false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = mBound.width();
        int height = mBound.height();
        setMeasuredDimension(width, height);
    }

    public void setPercent(float percent) {
        mPercent = percent;
        postInvalidate();
    }
}
