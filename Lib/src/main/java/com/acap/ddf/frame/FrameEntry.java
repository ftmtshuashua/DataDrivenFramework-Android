package com.acap.ddf.frame;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.acap.toolkit.Utils;
import com.acap.toolkit.log.LogUtils;
import com.weather.base.base.BaseFragment;

import java.text.MessageFormat;

/**
 * <pre>
 * Tip:
 *      储存Fragment的栈信息
 *
 * Created by ACap on 2021/1/6 18:10
 * </pre>
 */
public class FrameEntry {

    /**
     * 标记用户配置了容器；如果用户未手动配置一个容器，则会使用默认容器
     */
    public static final int FLAG_CONTAINER_USE = 0x1 << 31;

    /**
     * 转场信息，模拟Activity转场RequestCode逻辑，第16位用于内部转场处理
     */
    public static final int MASK_REQUEST_CODE = 0x7FFF;

    public static final int ACTION_ADD = 1;
    public static final int ACTION_REMOVE = 2;
    public static final int ACTION_DONE = 0;

    private int mFlag;
    private int action;

    // ----  容器
    private int c_layout;
    private String c_class;

    // ----  内容
    private String f_cls;
    private Bundle f_bundle;
    private String f_id;

    //---  Anima
    private int[] f_anim;

    private Fragment fragment; // 如果重建Fragment可能会新建

    public FrameEntry(FrameManager manager, Class<? extends BaseFragment> fcls) {
        this(manager, fcls.getName(), null);
    }

    public FrameEntry(FrameManager manager, Class<? extends BaseFragment> fcls, Bundle args) {
        this(manager, fcls.getName(), args);
    }

    public FrameEntry(FrameManager manager, String fcls, Bundle args) {
        action = ACTION_ADD;
        fragment = FrameUtils.instantiateFragment(manager.getActivity(), manager.getFragmentManager(), fcls, args);
        f_cls = fcls;
        f_id = Utils.getObjectId(fragment);
        f_bundle = args;
    }

    FrameEntry(FrameManager manager, StackCache cache) {
        mFlag = cache.mFlag;
        action = cache.action;
        c_layout = cache.c_layout;
        c_class = cache.c_class;
        f_cls = cache.f_cls;
        f_id = cache.f_id;
        f_bundle = cache.f_bundle;
        f_anim = cache.f_anim;
        fragment = manager.getFragmentManager().findFragmentByTag(getTag());
        if (fragment == null) {
            fragment = FrameUtils.instantiateFragment(manager.getActivity(), manager.getFragmentManager(), f_cls, f_bundle);
        }
    }


    /**
     * 设置容器
     */
    public void setContainer(Class<? extends Activity> cls, int layout) {
        c_class = cls.getName();
        c_layout = layout;
        mFlag |= FLAG_CONTAINER_USE;
    }

    /**
     * 设置容器
     */
    public void setContainer(FrameEntryContainer container) {
        setContainer(container.getCls(), container.getLayout());
    }

    public Class<? extends Activity> getContainerClass() {
        try {
            return (Class<? extends Activity>) Class.forName(c_class);
        } catch (ClassNotFoundException e) {
            LogUtils.e(e);
        }
        return null;
    }

    public int getContainerLayout() {
        return c_layout;
    }

    public void setRequestCode(int code) {
        if (code > MASK_REQUEST_CODE || code <= 0) throw new IllegalStateException(MessageFormat.format("requestCode的允许范围:{0} < code <= {1,number,0} -> 当前值:{2,number,0}", 0, MASK_REQUEST_CODE, code));
        mFlag = (mFlag & ~MASK_REQUEST_CODE) | code;
    }

    public int getRequestCode() {
        return mFlag & MASK_REQUEST_CODE;
    }

    public void setAnim(int[] anim) {
        if (anim == null || anim.length < 4) return;
        f_anim = anim;
    }

    public int[] getAnim() {
        return f_anim;
    }

    public int getFlag() {
        return mFlag;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    public boolean isDone() {
        return action == ACTION_DONE;
    }


    public Bundle getFragmentBundle() {
        return f_bundle;
    }

    public Class<? extends BaseFragment> getFragmentClass() {
        try {
            return (Class<? extends BaseFragment>) Class.forName(f_cls);
        } catch (ClassNotFoundException e) {
            LogUtils.e(e);
        }
        return null;
    }

    //获得Fragment的Tag信息
    public String getTag() {
        return f_cls + "-" + f_id;
    }

    public String getDebugInfo() {
        if (fragment == null) {
            return MessageFormat.format("Null-{0}", f_id);
        }
        return MessageFormat.format("{0}-{1}", fragment.getClass().getSimpleName(), f_id);
    }

    public Fragment getFragment() {
        return fragment;
    }


    public StackCache toCache() {
        return new StackCache(this);
    }

    //用于缓存Fragment的状态
    public static final class StackCache {
        private int mFlag;
        private int action;

        // ----  容器
        private int c_layout;
        private String c_class;

        // ----  内容
        private String f_cls;
        private Bundle f_bundle;
        private String f_id;

        private int[] f_anim;

        public StackCache(FrameEntry entry) {
            mFlag = entry.mFlag;
            action = entry.action;
            c_layout = entry.c_layout;
            c_class = entry.c_class;

            f_cls = entry.f_cls;
            f_id = entry.f_id;

            f_bundle = entry.f_bundle;
            f_anim = entry.f_anim;
        }
    }


    @Override
    public String toString() {
        return "F_StackEntry{" +
                "action=" + action +
                ", c_cls='" + f_cls + '\'' +
                ", f_id='" + f_id + '\'' +
                '}';
    }


}
