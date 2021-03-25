package com.acap.ddf.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.acap.toolkit.app.ContextUtils;
import com.weather.base.R;
import com.weather.base.frame.FrameEntry;
import com.weather.base.frame.FrameManager;
import com.weather.base.frame.IFrameProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Tip:
 *      单Activity架构中的Fragment
 *
 * Created by ACap on 2021/1/7 15:52
 * </pre>
 */
public class SingleFragment extends Fragment implements IFrameProvider {
    //无动画效果
    public static final int[] ANIM_NONE = new int[]{0, 0, 0, 0};
    //淡入淡出的动画效果
    public static final int[] ANIM_ALPHA = new int[]{R.anim.alpha_0_to_100, R.anim.alpha_100_to_0, R.anim.alpha_0_to_100, R.anim.alpha_100_to_0};

    /**
     * Standard activity result: operation canceled.
     */
    public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;
    /**
     * Standard activity result: operation succeeded.
     */
    public static final int RESULT_OK = Activity.RESULT_OK;
    /**
     * Start of user-defined activity results.
     */
    public static final int RESULT_FIRST_USER = Activity.RESULT_FIRST_USER;

    public SingleFragment() {
    }

    private FrameManager mFrameManager;

    private int mResultCode = Activity.RESULT_CANCELED;
    private Intent mResultData = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Activity activity = ContextUtils.getActivity(context);
        if (activity instanceof SingleActivity) {
            mFrameManager = ((SingleActivity) activity).getFrameManager();
        } else {
            mFrameManager = new FrameManager(activity);
        }

        if (mOnAttachOrDetachListener == null) return;
        for (OnAttachOrDetachListener listener : mOnAttachOrDetachListener)
            listener.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mFrameManager = null;
        if (mOnAttachOrDetachListener == null) return;
        for (OnAttachOrDetachListener listener : mOnAttachOrDetachListener)
            listener.onDetach();
    }


    public FrameManager getFrameManager() {
        return mFrameManager;
    }

    /**
     * 转场动画
     */
    @AnimatorRes
    @AnimRes
    private int[] trans_anim = new int[]{R.anim.h_100_to_0, R.anim.h_0_to_f100, R.anim.h_f100_to_0, R.anim.h_0_to_100};

    /**
     * 设置转场动画
     *
     * @param enter    A -> B : B的进入动画
     * @param exit     A -> B : A的退出动画
     * @param popEnter A <- B : A的进入动画
     * @param popExit  A <- B : B的退出动画
     */
    public void setTransAnim(@AnimatorRes @AnimRes int enter, @AnimatorRes @AnimRes int exit, @AnimatorRes @AnimRes int popEnter, @AnimatorRes @AnimRes int popExit) {
        this.trans_anim[0] = enter;
        this.trans_anim[1] = exit;
        this.trans_anim[2] = popEnter;
        this.trans_anim[3] = popExit;
    }

    /**
     * 设置转场动画
     *
     * @param anim [0]  A -> B : B的进入动画
     * @param anim [1]  A -> B : A的退出动画
     * @param anim [2]  A <- B : A的进入动画
     * @param anim [3]  A <- B : B的退出动画
     */
    public void setTransAnim(int[] anim) {
        setTransAnim(anim[0], anim[1], anim[2], anim[3]);
    }

    /**
     * 获得当前Fragment的转场动画
     */
    public int[] getTransAnim() {
        return trans_anim;
    }

    /**
     * Fragment出栈
     */
    public void finish() {
        getFrameManager().finish(this);
    }

    @Override
    public void toFragment(Class<? extends BaseFragment> fcls) {
        getFrameManager().toFragment(fcls);
    }

    @Override
    public void toFragment(Class<? extends BaseFragment> fcls, Bundle args) {
        getFrameManager().toFragment(fcls, args);
    }

    @Override
    public void toFragment(FrameEntry entry) {
        getFrameManager().toFragment(entry);
    }

    @Override
    public void toFragmentForResult(Class<? extends BaseFragment> fcls, int requestCode) {
        getFrameManager().toFragmentForResult(fcls, requestCode);
    }

    @Override
    public void toFragmentForResult(Class<? extends BaseFragment> fcls, Bundle args, int requestCode) {
        getFrameManager().toFragmentForResult(fcls, args, requestCode);
    }

    @Override
    public void toFragmentForResult(FrameEntry entry, int requestCode) {
        getFrameManager().toFragmentForResult(entry, requestCode);
    }

    /**
     * 判断当前Fragment是否处于栈顶
     */
    public boolean isTopFragment() {
        return this == getFrameManager().getTopFragment();
    }

    public final void setResult(int resultCode) {
        mResultCode = resultCode;
        mResultData = null;
    }

    public final void setResult(int resultCode, Intent data) {
        mResultCode = resultCode;
        mResultData = data;
    }

    public final int getResultCode() {
        return mResultCode;
    }

    public final Intent getResultData() {
        return mResultData;
    }


    private LifecycleEventObserver mFragmentResultEvent;

    //设置来之Fragment的回复
    public final void setFragmentResult(int requestCode, int resultCode, Intent data) {
        if (mFragmentResultEvent != null) {
            getLifecycle().removeObserver(mFragmentResultEvent);
            mFragmentResultEvent = null;
        }
        getLifecycle().addObserver(mFragmentResultEvent = new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    onActivityResult(requestCode, resultCode, data);
                    getLifecycle().removeObserver(this);
                    mFragmentResultEvent = null;
                }
            }
        });
    }


    /**
     * 转接 {android.app.Activity#onActivityResult(int, int, Intent)} 函数,当前Fragment位于栈顶时会收到该信号
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 转接 {@link Activity#onKeyDown(int, KeyEvent)} 函数,当前Fragment位于栈顶时会收到该信号
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }


    //Fragment附加与分离监听
    private List<OnAttachOrDetachListener> mOnAttachOrDetachListener;

    /**
     * 注册Fragment附加与分离监听
     */
    protected void registerOnAttachOrDetachListener(OnAttachOrDetachListener listener) {
        if (mOnAttachOrDetachListener == null) {
            mOnAttachOrDetachListener = new ArrayList<>();
        }
        mOnAttachOrDetachListener.add(listener);
    }

    /**
     * Fragment 附加与分离监听
     */
    protected interface OnAttachOrDetachListener {
        void onAttach(Context context);

        void onDetach();
    }


}
