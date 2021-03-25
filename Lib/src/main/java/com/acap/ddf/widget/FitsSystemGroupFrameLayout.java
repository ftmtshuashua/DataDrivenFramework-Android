package com.acap.ddf.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/**
 * <pre>
 * Tip:
 *      当Activity显示多个Fragment的时候，android:fitsSystemWindows属性只对第一个设置的对象生效。
 * 使用{@link FitsSystemGroupFrameLayout}作为根View可以修复多个 Fragment 在同一个 Activity 中
 * android:fitsSystemWindows 属性失效的问题。
 *
 * Created by ACap on 2021/1/20 15:25
 * </pre>
 */
public class FitsSystemGroupFrameLayout extends FrameLayout {

    public FitsSystemGroupFrameLayout(Context context) {
        this(context, null);
    }

    public FitsSystemGroupFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FitsSystemGroupFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                requestApplyInsets();
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++)
            getChildAt(index).dispatchApplyWindowInsets(insets);
        return insets;
    }

}