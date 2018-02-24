package com.qrx.pentagon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qrx
 * @date 2016/3/30
 * @time 15:08
 * @description
 */
public class CustomPentagonView extends View {

    /**
     * 背景画笔
     */
    private Paint mBgPaint;

    /**
     * 线条画笔
     */
    private Paint mLinePaint;

    /**
     * 标题画笔
     */
    private Paint mTitlePaint;

    /**
     * 粗白色线画笔
     */
    private Paint mBoldLinePaint;
    /**
     * 背景路径
     */
    private Path mBgPath;

    /**
     * 阴影路径
     */
    private Path mShadowPath;

    /**
     * 阴影画笔
     */
    private Paint mShadowPaint;

    /**
     * 高
     */
    private int height;
    /**
     * 宽
     */
    private int width;

    /**
     * 内边长
     */
    private int inSideLength;

    /**
     * 背景上下边距
     */
    private int paddingTopAndBottom = 0;

    /**
     * 背景各顶点数组
     */
    private PointF[] mBgPoints;

    /**
     * 背景各顶点数组
     */
    private PointF[] mDeepBgPoints;

    /**
     * 小标题数组
     */
    private PointF[] mTitlePoints;

    /**
     * 小白点的半径
     */
    private float circleRadius;

    /**
     * 顶点个数
     */
    private static final int POINT_COUNTS = 5;

    private final float sin36 = (float) Math.sin(getRadians(36));
    private final float sin54 = (float) Math.sin(getRadians(54));


    /**
     * 中心坐标
     */
    private PointF centerPoint;

    /**
     * 小标题
     */
   private String title[] = new String[]{"结交人脉", "维护人脉", "时间投入", "忙碌程度", "活跃"};

    /**
     * 数据 key标题  value值
     */
    private Map<String, Integer> data;

    /**
     * 各数据坐标
     */
    private PointF[] mDataPoints;

    /**
     * 用于测量文本大小
     */
    private Paint.FontMetrics fm;

    /**
     * 动画
     */
    private Animation mAnimation;

    private int mPaintAlpha;

    public CustomPentagonView(Context context) {
        this(context, null);
    }

    public CustomPentagonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomPentagonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode != MeasureSpec.UNSPECIFIED) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }
        height = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        paddingTopAndBottom = (int) ScreenUtil.dp2px(getContext(), 60);
        inSideLength = (int) ((double)(height - 2 * paddingTopAndBottom) / (double)(1 + sin54));
        circleRadius = ScreenUtil.dp2px(getContext(), 4);
        centerPoint = new PointF(w / 2, inSideLength + paddingTopAndBottom);

    }

    /**设置数据 key-->顶点标题   value --> 数值[0,100]
     * @param data 数据map
     */
    public void setData(Map<String, Integer> data) {
        if (data != null) {
            this.data = data;
        }
        initAnimation();
    }

    /**
     * 动画
     */
    private void initAnimation() {
        if (mAnimation == null) {
            mAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    mPaintAlpha = (int) (interpolatedTime * 255);
                    invalidate();
                }
            };
            mAnimation.setInterpolator(new DecelerateInterpolator(3.2f));
        }

        if (!mAnimation.hasStarted()) {
            mAnimation.cancel();
        }
        mAnimation.setDuration(10000);
        startAnimation(mAnimation);
    }

    /**
     * 度数转换成弧度
     *
     * @param degrees
     * @return
     */
    private double getRadians(int degrees) {
        return degrees * Math.PI / 180;
    }

    /**
     * 初始化
     */
    private void init() {
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPath = new Path();

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setTextSize(ScreenUtil.dp2px(getContext(), 14));

        fm = mTitlePaint.getFontMetrics();

        mShadowPath = new Path();

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setColor(getContext().getResources().getColor(R.color.white));
        mShadowPaint.setAlpha(100);

        mBoldLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBoldLinePaint.setStyle(Paint.Style.STROKE);
        mBoldLinePaint.setColor(getContext().getResources().getColor(R.color.white));
        mBoldLinePaint.setStrokeWidth(ScreenUtil.dp2px(getContext(), 2));

        ////
        data = new HashMap<>();
        data.put("结交人脉", 10);
        data.put("维护人脉", 20);
        data.put("时间投入", 30);
        data.put("忙碌程度", 40);
        data.put("活跃", 50);

        initAnimation();
    }

    /**
     * 画背景
     */
    private void drawBackground(Canvas canvas) {
        initBgPoints();
        mBgPath.reset();
        mBgPath.moveTo(mBgPoints[0].x, mBgPoints[0].y);//第一个点 顶点
        for (int i = 1; i < POINT_COUNTS; i++) {
            mBgPath.lineTo(mBgPoints[i].x, mBgPoints[i].y);
        }
        mBgPath.close();
        mBgPaint.setColor(getContext().getResources().getColor(R.color.bg_blue_color));
        mBgPaint.setAlpha(mPaintAlpha);
        canvas.drawPath(mBgPath, mBgPaint);
    }

    /**
     * 画背景
     */
    private void drawDeepBackground(Canvas canvas) {
        initDeepBgPoints();
        mBgPath.reset();
        mBgPath.moveTo(mDeepBgPoints[0].x, mDeepBgPoints[0].y);//第一个点 顶点
        for (int i = 1; i < POINT_COUNTS; i++) {
            mBgPath.lineTo(mDeepBgPoints[i].x, mDeepBgPoints[i].y);
        }
        mBgPath.close();
        mBgPaint.setColor(getContext().getResources().getColor(R.color.bg_blue_deep_color));
        mBgPaint.setAlpha(mPaintAlpha);
        canvas.drawPath(mBgPath, mBgPaint);
    }

    /**
     * 初始化深色背景各顶点
     */
    private void initDeepBgPoints() {
        initBgPoints();
        if (mDeepBgPoints == null) {
            mDeepBgPoints = new PointF[POINT_COUNTS];
            for (int i = 0; i < POINT_COUNTS; i++) {
                mDeepBgPoints[i] = getMiddlePointFromCentre(mBgPoints[i]);
            }
        }
    }

    /**
     * 画网
     *
     * @param canvas
     */
    private void drawMesh(Canvas canvas) {
        mLinePaint.setColor(getContext().getResources().getColor(R.color.white));
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setAlpha(mPaintAlpha);
        for (int i = 0; i < POINT_COUNTS; i++) {
            canvas.drawLine(centerPoint.x, centerPoint.y, mBgPoints[i].x, mBgPoints[i].y, mLinePaint);
        }
    }


    /**
     * 获取两个点的中点
     *
     * @param p1 第一个点
     * @param p2 第二个点
     * @return
     */
    private PointF getMiddlePoint(PointF p1, PointF p2) {
        return new PointF((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }


    /**
     * 获取两个点的中点
     *
     * @param p1 第一个点
     * @return
     */
    private PointF getMiddlePointFromCentre(PointF p1) {
        return new PointF((p1.x + centerPoint.x) / 2, (p1.y + centerPoint.y) / 2);
    }

    /**
     * 初始化各顶点
     */
    private void initBgPoints() {
        if (mBgPoints == null) {
            mBgPoints = new PointF[POINT_COUNTS];
            mBgPoints[0] = new PointF(width / 2, paddingTopAndBottom);
            mBgPoints[1] = new PointF((float) ((double)width / 2 + (double)(inSideLength * 2 * sin36 * sin54)), (float) ((double)(inSideLength * 2 * sin36 * sin36) + (double)(paddingTopAndBottom)));
            mBgPoints[2] = new PointF((float) ((double)width / 2 +(double)(inSideLength * sin36)), (float) ((double)inSideLength * (1 + sin54) + (double)paddingTopAndBottom));
            mBgPoints[3] = new PointF((float) ((double)width / 2 - (double)inSideLength * sin36), (float) ((double)inSideLength * (1 + sin54) + (double)paddingTopAndBottom));
            mBgPoints[4] = new PointF((float) ((double)width / 2 - (double)inSideLength * 2 * sin36 * sin54), (float) ((double)inSideLength * 2 * sin36 * sin36 + (double)paddingTopAndBottom));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawDeepBackground(canvas);
        drawMesh(canvas);
        drawTitle(canvas);
        drawCoordinate(canvas);
        drawShadow(canvas);
        drawBoldLine(canvas);
    }

    /**
     * 画粗线
     *
     * @param canvas
     */
    private void drawBoldLine(Canvas canvas) {
        mBoldLinePaint.setAlpha(mPaintAlpha);
        for (int i = 0; i < POINT_COUNTS; i++) {
            if (mDataPoints[i].y != centerPoint.y
                    && mDataPoints[(i + 1) % POINT_COUNTS].y != centerPoint.y) {
                canvas.drawLine(mDataPoints[i].x, mDataPoints[i].y,
                        mDataPoints[(i + 1) % POINT_COUNTS].x,
                        mDataPoints[(i + 1) % POINT_COUNTS].y,
                        mBoldLinePaint);
            }
        }
    }

    /**
     * 画阴影
     *
     * @param canvas
     */
    private void drawShadow(Canvas canvas) {
        mShadowPaint.setAlpha(mPaintAlpha*100/255);
        mShadowPath.moveTo(mDataPoints[0].x, mDataPoints[0].y);
        for (int i = 1; i < POINT_COUNTS; i++) {
            mShadowPath.lineTo(mDataPoints[i].x, mDataPoints[i].y);
        }
        mShadowPath.close();
        canvas.drawPath(mShadowPath, mShadowPaint);
    }

    /**
     * 画具体坐标
     *
     * @param canvas
     */
    private void drawCoordinate(Canvas canvas) {
        initDataPoints();
        mLinePaint.setAlpha(mPaintAlpha);
        for (int i = 0; i < POINT_COUNTS; i++) {
            if (mDataPoints[i].y != centerPoint.y) {
                //排除数据为0的情况
                //画圆
                canvas.drawCircle(mDataPoints[i].x, mDataPoints[i].y, circleRadius, mLinePaint);
            }
        }

    }

    /**
     * 初始化坐标
     */
    private void initDataPoints() {
        mDataPoints = new PointF[POINT_COUNTS];
        for (int i = 0; i < POINT_COUNTS; i++) {
            mDataPoints[i] = getDataPoint(mBgPoints[i], data.get(title[i]));
        }
    }

    /**
     * 获取指定数值的点
     *
     * @param p     顶点
     * @param value 数值
     * @return
     */
    private PointF getDataPoint(PointF p, int value) {
        PointF pointF = new PointF();
        pointF.x = (float) ((value * 1.0 / 100 * inSideLength) / inSideLength * (p.x - centerPoint.x) + centerPoint.x);
        pointF.y = (float) ((value * 1.0 / 100 * inSideLength) / inSideLength * (p.y - centerPoint.y) + centerPoint.y);
        return pointF;
    }

    /**
     * 获取两点之间的距离
     *
     * @param p1 第一点
     * @param p2 第二点
     * @return
     */
    private float getDistance(PointF p1, PointF p2) {
        return (float) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    /**
     * 画小标题
     *
     * @param canvas
     */
    private void drawTitle(Canvas canvas) {
        initTitlePoints();

        //小标题
        mTitlePaint.setColor(getContext().getResources().getColor(R.color.text_color_black));
        mTitlePaint.setAlpha(mPaintAlpha);
        for (int i = 0; i < POINT_COUNTS; i++) {
            canvas.drawText(title[i], mTitlePoints[i].x - mTitlePaint.measureText(title[i]) / 2, mTitlePoints[i].y, mTitlePaint);
        }

        //蓝色数值
        mTitlePaint.setColor(getContext().getResources().getColor(R.color.text_color_blue));
        mTitlePaint.setAlpha(mPaintAlpha);
        for (int i = 0; i < POINT_COUNTS; i++) {
            String text;
            if (data.get(title[i]) > 0) {
                text = String.valueOf(data.get(title[i]));
            } else {
                text = "N/A";
            }

            canvas.drawText(text, mTitlePoints[i].x - mTitlePaint.measureText(text) / 2, mTitlePoints[i].y + (fm.bottom - fm.top), mTitlePaint);
        }
    }

    /**
     * 初始化各小标题位置
     */
    private void initTitlePoints() {
        if (mTitlePoints == null) {
            mTitlePoints = new PointF[POINT_COUNTS];
            for (int i = 0; i < POINT_COUNTS; i++) {
                mTitlePoints[i] = getTitlePoint(mBgPoints[i]);
            }
        }
    }

    /**
     * 通过传入的顶点坐标  获取小标题坐标
     *
     * @param pointF
     * @return
     */
    private PointF getTitlePoint(PointF pointF) {
        return getDataPoint(pointF, 125);
    }


}
