package com.acap.ddf.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <pre>
 * Tip:
 *      适配FitsSystemWindows属性，防止配置透明导航栏的时候导致View底部被设置Padding
 *
 * Created by ACap on 2021/1/20 17:48
 * </pre>
 */
public class FitsSystemFrameLayout extends FrameLayout {
    public FitsSystemFrameLayout(@NonNull Context context) {
        super(context);
        setFitsSystemWindows(true);
    }

    public FitsSystemFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFitsSystemWindows(true);
    }

    public FitsSystemFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFitsSystemWindows(true);
    }

}
