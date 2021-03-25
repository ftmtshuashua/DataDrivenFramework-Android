package com.acap.ddf.frame;

import android.app.Activity;

import androidx.annotation.IdRes;

import java.io.Serializable;

/**
 * <pre>
 * Tip:
 *      用于显示Fragment的目标
 *
 * Created by ACap on 2021/1/15 18:05
 * </pre>
 */
public class FrameEntryContainer implements Serializable {

    public static final FrameEntryContainer DEFAULT = new FrameEntryContainer(FrameActivity.class, FrameActivity.Fragment_Content_ID);


    private Class<? extends Activity> cls;
    @IdRes
    private int layout;

    public FrameEntryContainer(Class<? extends Activity> cls, @IdRes int layout) {
        this.layout = layout;
        this.cls = cls;
    }

    public int getLayout() {
        return layout;
    }

    public Class<? extends Activity> getCls() {
        return cls;
    }

    //判断Class是否相同
    public boolean isSame(Class<?> cls) {
        return this.cls == cls;
    }

}
