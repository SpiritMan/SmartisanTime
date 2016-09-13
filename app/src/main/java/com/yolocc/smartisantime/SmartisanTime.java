package com.yolocc.smartisantime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Calendar;

/**
 */
public class SmartisanTime extends View {

    //表盘的背景颜色
    @ColorInt
    private static final int DIAL_BG = 0xFFF0F0F0;
    //表外圆环的颜色
    @ColorInt
    private static final int RING_BG = 0xFFF8F8F8;
    //字体颜色
    @ColorInt
    private static final int TEXT_COLOR = 0xFF141414;
    //时针和分针的颜色
    @ColorInt
    private static final int HOUR_MINUTE_COLOR = 0xFF5B5B5B;
    //秒钟的颜色
    @ColorInt
    private static final int SECOND_COLOR = 0xFFB55050;
    //表盘最小大小
    private static final int MIN_SIZE = 200;

    private static final int HOUR_MINUTE_WIDTH = 30;

    private static final int SECOND_WIDTH = 8;

    //每秒 秒针移动6°
    private static final int DEGREE = 6;

    private int hour, minute, second;

    //圆环的宽度
    private int ringPaintWidth = 10;
    //表盘的大小
    private int mSize;
    //表盘背景画笔
    private Paint outCirclePaint;
    //最外层圆环
    private Paint outRingPaint;
    //时间文本
    private Paint timeTextPaint;
    //时针,分针,秒针
    private Paint hourPaint, minutePaint, secondPaint;
    //中心圆
    private Paint inCirclePaint, inRedCirclePaint;

    /**
     * 日历类，用来获取当前时间
     */
    private Calendar calendar;

    public SmartisanTime(Context context) {
        super(context);
        initPaint();
    }

    public SmartisanTime(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public SmartisanTime(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        //获取当前时间的实例
        calendar = Calendar.getInstance();

        outCirclePaint = new Paint();
        outCirclePaint.setColor(DIAL_BG);
        outCirclePaint.setAntiAlias(true);

        outRingPaint = new Paint();
        outRingPaint.setColor(RING_BG);
        outRingPaint.setStrokeWidth(dp2px(ringPaintWidth));
        outRingPaint.setStyle(Paint.Style.STROKE);
        outRingPaint.setAntiAlias(true);
        //        添加阴影 0x80000000
        outRingPaint.setShadowLayer(4, 2, 2, 0x80000000);

        timeTextPaint = new Paint();
        timeTextPaint.setAntiAlias(true);
        timeTextPaint.setColor(TEXT_COLOR);

        hourPaint = new Paint();
        hourPaint.setAntiAlias(true);
        hourPaint.setColor(HOUR_MINUTE_COLOR);
        hourPaint.setStrokeWidth(HOUR_MINUTE_WIDTH);
        //设置为圆角
        hourPaint.setStrokeCap(Paint.Cap.ROUND);
        //        添加阴影
        hourPaint.setShadowLayer(4, 0, 0, 0x80000000);

        minutePaint = new Paint();
        minutePaint.setAntiAlias(true);
        minutePaint.setColor(HOUR_MINUTE_COLOR);
        minutePaint.setStrokeWidth(HOUR_MINUTE_WIDTH);
        //设置为圆角
        minutePaint.setStrokeCap(Paint.Cap.ROUND);
        //        添加阴影
        minutePaint.setShadowLayer(4, 0, 0, 0x80000000);

        secondPaint = new Paint();
        secondPaint.setAntiAlias(true);
        secondPaint.setColor(SECOND_COLOR);
        secondPaint.setStrokeWidth(SECOND_WIDTH);
        //设置为圆角
        secondPaint.setStrokeCap(Paint.Cap.ROUND);
        //        添加阴影
        secondPaint.setShadowLayer(4, 3, 0, 0x80000000);

        inCirclePaint = new Paint();
        inCirclePaint.setAntiAlias(true);
        inCirclePaint.setColor(HOUR_MINUTE_COLOR);
        //        添加阴影
        inCirclePaint.setShadowLayer(5, 0, 0, 0x80000000);

        inRedCirclePaint = new Paint();
        inRedCirclePaint.setAntiAlias(true);
        inRedCirclePaint.setColor(SECOND_COLOR);
        //        添加阴影
        inRedCirclePaint.setShadowLayer(5, 0, 0, 0x80000000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = startMeasure(widthMeasureSpec);
//        int height = startMeasure(heightMeasureSpec);
        mSize = dp2px(MIN_SIZE);
        setMeasuredDimension(mSize, mSize);
    }

    /**
     * 根据不同的模式,设置控件的大小;
     *
     * @param whSpec
     * @return 最后控件的大小
     */
    private int startMeasure(int whSpec) {
        int result;
        int size = MeasureSpec.getSize(whSpec);
        int mode = MeasureSpec.getMode(whSpec);
        if (mode == MeasureSpec.EXACTLY || mode == MeasureSpec.AT_MOST) {
            if (size < dp2px(MIN_SIZE)) {
                result = dp2px(MIN_SIZE);
            } else {
                result = size;
            }
        } else {
            result = dp2px(MIN_SIZE);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calendar = Calendar.getInstance();
        getTime();
        //将画布移到中央
        canvas.translate(mSize / 2, mSize / 2);

        drawOutCircle(canvas);

        drawOutRing(canvas);

        drawScale(canvas);

        drawHour(canvas);

        drawMinute(canvas);

        drawInCircle(canvas);

        drawSecond(canvas);

        drawInRedCircle(canvas);

        //每隔1秒重绘View,重绘会调用onDraw()方法
        postInvalidateDelayed(1000);
    }

    /**
     * 获取时分秒
     */
    private void getTime() {
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        System.out.println(hour + ":" + minute + ":" + second);
    }

    /**
     * 画表盘背景
     *
     * @param canvas 画布
     */
    private void drawOutCircle(Canvas canvas) {
        canvas.drawCircle(0, 0, mSize / 2 - 4, outCirclePaint);
        canvas.save();
    }


    /**
     * 画表盘最外层圆环
     *
     * @param canvas 画布
     */
    private void drawOutRing(Canvas canvas) {
        canvas.save();
        float radius = mSize / 2 - dp2px(ringPaintWidth + 6) / 2;
        RectF rectF = new RectF(-radius, -radius, radius, radius);
        outRingPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(rectF, 0, 360, false, outRingPaint);

        canvas.restore();
    }


    /**
     * 画时间标志
     *
     * @param canvas 画布
     */
    private void drawScale(Canvas canvas) {
        int radius = mSize / 2 - dp2px(ringPaintWidth) / 2;
        // 刻度弧紧靠进度弧
        canvas.save();
        int textSize = 15;
        timeTextPaint.setTextSize(sp2px(textSize));
        float scaleWidth = timeTextPaint.measureText("12");
        canvas.drawText("12", -scaleWidth / 2, -radius + 30 + dp2px(textSize), timeTextPaint);
        canvas.rotate(90, 0, 0);
        scaleWidth = timeTextPaint.measureText("3");
        canvas.drawText("3", -scaleWidth / 2, -radius + 30 + dp2px(textSize), timeTextPaint);
        canvas.rotate(-90, 0, 0);
        canvas.restore();
    }

    /**
     * 画时针
     *
     * @param canvas 画布
     */
    private void drawHour(Canvas canvas) {
        int length = mSize / 4;
        canvas.save();
        //这里没有算秒钟对时钟的影响
        float degree = hour * 5 * DEGREE + minute / 2;
        canvas.rotate(degree, 0, 0);
        canvas.drawLine(0, 0, 0, -length, hourPaint);
        canvas.restore();
    }

    /**
     * 画分针
     *
     * @param canvas 画布
     */
    private void drawMinute(Canvas canvas) {
        int length = mSize / 3;
        canvas.save();
        float degree = minute * DEGREE + second / 10;
        canvas.rotate(degree, 0, 0);
        canvas.drawLine(0, 0, 0, -length, minutePaint);
        canvas.restore();
    }

    /**
     * 画中心黑圆
     *
     * @param canvas 画布
     */
    private void drawInCircle(Canvas canvas) {
        int radius = mSize / 20;
        canvas.save();
        canvas.drawCircle(0, 0, radius, inCirclePaint);
        canvas.restore();
    }

    /**
     * 红色中心圆
     *
     * @param canvas 画布
     */
    private void drawInRedCircle(Canvas canvas) {
        int radius = mSize / 40;
        canvas.save();
        canvas.drawCircle(0, 0, radius, inRedCirclePaint);
        canvas.restore();
    }

    /**
     * 画秒针
     *
     * @param canvas 画布
     */
    private void drawSecond(Canvas canvas) {
        int length = mSize / 2;
        canvas.save();
        canvas.rotate(second * DEGREE);
        canvas.drawLine(0, length / 5, 0, -length * 4 / 5, secondPaint);
        canvas.restore();
    }

    /**
     * 将 dp 转换为 px
     *
     * @param dp 需转换数
     * @return 返回转换结果
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}
