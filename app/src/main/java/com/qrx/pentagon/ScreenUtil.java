package com.qrx.pentagon;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * 屏幕尺寸工具类
 */
public class ScreenUtil {

    private ScreenUtil() {}

    /**
     * dp转像素
     * @param context
     * @param dp
     * @return
     */
    public static float dp2px(Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static DisplayMetrics getMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static float getDensity(Context context) {
        return getMetrics( context ).density;
    }

    public static float getScaleDensity(Context context) {
        return getMetrics( context ).scaledDensity;
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return getMetrics( context ).widthPixels;
    }

    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return getMetrics( context ).heightPixels;
    }

    private volatile static Rect sTextBounds = new Rect();
    /**
     * 获取 绘制文本的尺寸大小
     * @param text
     * @param paint
     * @return
     */
    public static float[] getMeasuredTextBounds(String text, Paint paint) {
        synchronized ( sTextBounds ) {
            final int index = 0;
            final int length = text.length();
            float w = paint.measureText( text, index, length );
            paint.getTextBounds( text, index, length, sTextBounds );
            float h = sTextBounds.bottom - sTextBounds.top;
            return new float[] { w, h };
        }
    }

}
