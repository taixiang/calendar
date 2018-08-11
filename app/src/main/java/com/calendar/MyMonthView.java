package com.calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;

public class MyMonthView extends MonthView {

    private Paint paint1 = new Paint();
    private Paint paint2 = new Paint();
    private Context context;
    private Bitmap dayBgBitmap;
    private Bitmap daySuccessBitmap;

    public MyMonthView(Context context) {
        super(context);
        this.context = context;
        //取消文字加粗
        mCurMonthTextPaint.setFakeBoldText(false);
        mOtherMonthTextPaint.setFakeBoldText(false);

        paint1.setColor(ContextCompat.getColor(context, R.color.green));
        paint1.setTextSize(spToPx(context, 13));
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setAntiAlias(true);
        paint1.setTextAlign(Paint.Align.CENTER);

        paint2.setColor(ContextCompat.getColor(context, R.color.white));
        paint2.setTextSize(spToPx(context, 13));
        paint2.setAntiAlias(true);
        paint2.setTextAlign(Paint.Align.CENTER);

        dayBgBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.day_bg);
        daySuccessBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.day_success);

    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        return false;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {

    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        //这里的x、y 是每日的起点坐标
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        if ("1".equals(calendar.getScheme())) {
            // 不可完成的，绘制圆
            paint1.setStrokeWidth(0);
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, paint1);
            paint1.setStrokeWidth(dpToPx(context, 1));
            canvas.drawCircle(cx, cy + 3, mItemWidth / 4 - 9, paint1);
        } else if ("2".equals(calendar.getScheme())) {
            //可以完成的，先绘制背景图再写文字，防止文字被遮住
            canvas.drawBitmap(dayBgBitmap, x + mItemWidth / 4 - 5, y + mItemHeight / 4 + 8, paint2);
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, paint2);
        } else if ("3".equals(calendar.getScheme())) {
            //今日已完成的，绘制圆+打勾图片
            paint1.setStrokeWidth(0);
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, paint1);
            paint1.setStrokeWidth(dpToPx(context, 1));
            canvas.drawCircle(cx, cy + 3, mItemWidth / 4 - 9, paint1);
            canvas.drawBitmap(daySuccessBitmap, x + mItemWidth * 3 / 4 - 18, y + mItemHeight * 3 / 4 - 24, paint1);
        } else if ("4".equals(calendar.getScheme())) {
            //历史已完成的，绘制打勾图片
            paint1.setStrokeWidth(0);
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, paint1);
            canvas.drawBitmap(daySuccessBitmap, x + mItemWidth * 3 / 4 - 18, y + mItemHeight * 3 / 4 - 40, paint1);
        } else {
            //正常日期的显示
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }

    public static int spToPx(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dpToPx(Context context, float dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
