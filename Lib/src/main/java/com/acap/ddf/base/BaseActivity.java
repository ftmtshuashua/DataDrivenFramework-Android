package com.acap.ddf.base;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.lfp.eventtree.OnEventSucceedListener;
import com.weather.base.BuildConfig;
import com.weather.base.base.SingleActivity;
import com.weather.base.base.support.ActivityInitListener;
import com.weather.base.base.support.BindViewUtils;
import com.weather.base.base.support.IViewModelProvider;
import com.weather.base.base.support.LifecycleLog;
import com.weather.base.base.support.WindowLock;
import com.weather.base.base.support.WindowLockStoreOwner;

/**
 * BaseActivity
 * <p>
 * <br/>
 * Author:Hope_LFB<br/>
 * Time:2020/12/8 15:21
 */
public class BaseActivity<ViewDataBind extends ViewDataBinding> extends SingleActivity implements IViewModelProvider, WindowLockStoreOwner {

    /**
     * 当配置了该标志，者拦截Activity的finish事件:{@link #onBackPressed()}
     */
    public static final int FLAG_INTERCEPT_BACK_PRESSED = 0x1;

    public BaseActivity() {
        if (BuildConfig.DEBUG) {
            getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> LifecycleLog.log(BaseActivity.this, event.toString()));
        }
    }

    private int mDataBindingLayout;
    protected ViewDataBind mViewDataBind;
    /*窗口锁*/
    private WindowLock mWindowLock;
    /*ViewModel数据提供者*/
    private ViewModelProvider mVMP_Activity, mVMP_Application;

    private int mFlag;

    public void setDataBindingLayout(int mDataBindingLayout) {
        this.mDataBindingLayout = mDataBindingLayout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int layout_id = BindViewUtils.INSTANCE.geBindView(this);
        if (layout_id > 0) {
            setContentView(layout_id);
        } else {
            layout_id = BindViewUtils.INSTANCE.getDataBindingLayout(this);
            if (layout_id <= 0) layout_id = mDataBindingLayout;
            if (layout_id > 0) {
                mViewDataBind = DataBindingUtil.setContentView(this, layout_id);
                mViewDataBind.setLifecycleOwner(this);
            }
        }

        //APP启动监听
        new ActivityInitListener(this)
                .addOnEventListener((OnEventSucceedListener) () -> onActivityInited())
                .start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LifecycleLog.log(this, "onRestart()");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        LifecycleLog.log(this, "onAttachedToWindow()");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LifecycleLog.log(this, "onDetachedFromWindow()");
    }

    public Context getContext() {
        return this;
    }

    public BaseActivity getActivity() {
        return this;
    }

    public ViewGroup getContentView() {
        return findViewById(android.R.id.content);
    }

    /**
     * 设置BaseActivity的配置项
     *
     * @param flag
     */
    public void setFlag(int flag) {
        mFlag = flag;
    }


    /**
     * 获得通过 DataBindingLayout 绑定 ViewDataBind 实例
     *
     * @return ViewDataBinding
     */
    protected ViewDataBind getViewDataBind() {
        return mViewDataBind;
    }


    @Override
    public <T extends ViewModel> T getViewModelAtActivity(Class<T> modelClass) {
        if (mVMP_Activity == null) mVMP_Activity = new ViewModelProvider(this);
        return (T) mVMP_Activity.get(modelClass);
    }


    @Override
    public <T extends ViewModel> T getViewModelAtApplication(Class<T> modelClass) {
        if (mVMP_Application == null) {
            Application application = getApplication();
            if (application != null) {
                if (application instanceof ViewModelStoreOwner) {
                    mVMP_Application = new ViewModelProvider((ViewModelStoreOwner) application);
                } else {
                    throw new NullPointerException("Application 未实现 ViewModelStoreOwner!");
                }
            } else {
                throw new NullPointerException("未找到 Application!");
            }
        }
        return mVMP_Application.get(modelClass);
    }


    /**
     * 当 Activity UI构建完成时回调该方法
     */
    protected void onActivityInited() {

    }

    @Override
    public WindowLock getWindowLock() {
        if (mWindowLock == null) mWindowLock = new WindowLock();
        return mWindowLock;
    }

    @Override
    public void onBackPressed() {
        if ((mFlag & FLAG_INTERCEPT_BACK_PRESSED) == 0) {
            super.onBackPressed();
        }
    }

}