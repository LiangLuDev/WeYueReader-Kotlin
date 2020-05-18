package com.page.view.utils;

import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by newbiechen on 17-5-1.
 */

public class ScreenUtils {

    public static int dpToPx(int dp) {
        DisplayMetrics metrics = getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }


    public static int spToPx(int sp) {
        DisplayMetrics metrics = getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }


    public static DisplayMetrics getDisplayMetrics() {
        return BookUtils.mAppContext
                .getResources()
                .getDisplayMetrics();
    }
}
