package com.rajesh.pdfdemo.constant;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * Created by zhufeng on 2017/3/1.
 */

public class DeviceConstants {
    private static DeviceConstants instance = null;
    public static int DP;
    public static int SP;
    /**
     * 屏幕宽
     */
    public static int SCREEN_WIDTH;
    /**
     * 屏幕高
     * 有底部导航栏时，为导航栏上面的高度
     */
    public static int SCREEN_HEIGHT;
    /**
     * actionbar高度
     */
    public static int ACTION_BAR_HEIGHT;
    /**
     * 状态栏高度
     */
    public static int STATUS_BAR_HEIGHT;
    /**
     * 底部导航栏高度
     */
    public static int NAVIGATION_BAR_HEIGHT;

    private DeviceConstants() {
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new DeviceConstants();
            setDp(context);
            setSp(context);
            setScreenWidth(context);
            setScreenHeight(context);
            setActionBarHeight(context);
            setStatusBarHeight(context);
            setNavigationBarHeight(context);
            Log.v("zhufeng", "DP:" + DP + "\n"
                    + "SP:" + SP + "\n"
                    + "SCREEN_WIDTH:" + SCREEN_WIDTH + "\n"
                    + "SCREEN_HEIGHT:" + SCREEN_HEIGHT + "\n"
                    + "STATUS_BAR_HEIGHT:" + STATUS_BAR_HEIGHT + "\n"
                    + "ACTION_BAR_HEIGHT:" + ACTION_BAR_HEIGHT + "\n"
                    + "NAVIGATION_BAR_HEIGHT:" + NAVIGATION_BAR_HEIGHT);
        }
    }

    private static void setDp(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        DP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm);
    }

    private static void setSp(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        SP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1, dm);
    }

    private static void setScreenWidth(Context context) {
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        SCREEN_WIDTH = metric.widthPixels;
    }

    private static void setScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        SCREEN_HEIGHT = outMetrics.heightPixels;
    }

    private static void setActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            ACTION_BAR_HEIGHT = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        if (ACTION_BAR_HEIGHT == 0) {
            ACTION_BAR_HEIGHT = 45 * DP;
        }
    }

    private static void setStatusBarHeight(Context context) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            STATUS_BAR_HEIGHT = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            STATUS_BAR_HEIGHT = 0;
            e.printStackTrace();
        }
    }

    private static void setNavigationBarHeight(Context context) {
        int totalHeight = getDpi(context);
        NAVIGATION_BAR_HEIGHT = totalHeight - SCREEN_HEIGHT;
    }

    /**
     * 获取屏幕原始尺寸高度，包括虚拟功能键高度
     *
     * @param context
     * @return
     */
    private static int getDpi(Context context) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

}
