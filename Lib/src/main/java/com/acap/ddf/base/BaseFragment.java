package com.acap.ddf.base;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.acap.toolkit.app.AppUtils;
import com.weather.base.BuildConfig;
import com.weather.base.base.SingleFragment;
import com.weather.base.base.support.BindViewUtils;
import com.weather.base.base.support.IViewModelProviderAtFragment;
import com.weather.base.base.support.LifecycleLog;
import com.weather.base.base.support.WindowLock;
import com.weather.base.base.support.WindowLockStoreOwner;

/**
 * BaseFragment
 * <p>
 * <br/>
 * Author:Hope_LFB<br/>
 * Time:2020/12/10 17:51
 */
public class BaseFragment<ViewDataBind extends ViewDataBinding> extends SingleFragment implements IViewModelProviderAtFragment, WindowLockStoreOwner {

    public BaseFragment() {
        if (BuildConfig.DEBUG) {
            getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> LifecycleLog.log(BaseFragment.this, event.toString()));
        }

        registerOnAttachOrDetachListener(OnWindowLockState);
        registerOnAttachOrDetachListener(OnViewModelState);
    }

    private int mDataBindingLayout;
    protected ViewDataBind mViewDataBind;

    public void setDataBindingLayout(int mDataBindingLayout) {
        this.mDataBindingLayout = mDataBindingLayout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) LifecycleLog.log(this, "onCreateView()");
        int layout_id = BindViewUtils.INSTANCE.geBindView(this);
        if (layout_id > 0) {
            return inflater.inflate(layout_id, container, false);
        } else {
            layout_id = BindViewUtils.INSTANCE.getDataBindingLayout(this);
            if (layout_id <= 0) layout_id = mDataBindingLayout;
            if (layout_id > 0) {
                mViewDataBind = DataBindingUtil.inflate(inflater, layout_id, container, false);
                mViewDataBind.setLifecycleOwner(this);
                return mViewDataBind.getRoot();
            }
        }
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView = view;
        mRootView.setClickable(true);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        if (BuildConfig.DEBUG) LifecycleLog.log(this, "onAttach()");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        if (BuildConfig.DEBUG) LifecycleLog.log(this, "onDetach()");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (BuildConfig.DEBUG) LifecycleLog.log(this, "onDestroyView()");
    }

    private View mRootView;

    public <T extends View> T findViewById(@IdRes int id) {
        return mRootView == null ? null : mRootView.findViewById(id);
    }


    private ViewModelStoreOwner mProviderOwner;
    private ViewModelProvider mVMP_Activity, mVMP_Application, mVMP_Fragment;
    private OnAttachOrDetachListener OnViewModelState = new OnAttachOrDetachListener() {

        @Override
        public void onAttach(Context context) {
            ViewModelStoreOwner owner = null;
            if (context instanceof ViewModelStoreOwner) {
                owner = (ViewModelStoreOwner) context;
            }
            if (owner != null) {
                if (mProviderOwner == null) {
                    mProviderOwner = owner;
                } else if (mProviderOwner != owner) {
                    clearActivityViewModelStore();
                    mProviderOwner = owner;
                }
            } else {
                mProviderOwner = null;
            }
        }

        @Override
        public void onDetach() {
            clearActivityViewModelStore();
        }

        //清理ViewModel相关
        private void clearActivityViewModelStore() {
            mProviderOwner = null;
            mVMP_Activity = null;
        }

    };

    @Override
    public <T extends ViewModel> T getViewModelAtFragment(Class<T> modelClass) {
        if (mVMP_Fragment == null) mVMP_Fragment = new ViewModelProvider(this);
        return mVMP_Fragment.get(modelClass);
    }

    @Override
    public <T extends ViewModel> T getViewModelAtApplication(Class<T> modelClass) {
        if (mVMP_Application == null) {
            Application application = AppUtils.getApp();
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

    @Override
    public <T extends ViewModel> T getViewModelAtActivity(Class<T> modelClass) {
        if (mVMP_Activity == null) {
            if (mProviderOwner != null) {
                mVMP_Activity = new ViewModelProvider(mProviderOwner);
            } else {
                throw new NullPointerException("Fragment还未绑定到一个Activity!");
            }
        }
        return mVMP_Activity.get(modelClass);
    }


    /*窗口锁*/
    private WindowLock mWindowLock;
    private WindowLockStoreOwner mWindowLockStoreOwner;
    private OnAttachOrDetachListener OnWindowLockState = new OnAttachOrDetachListener() {
        @Override
        public void onAttach(Context context) {
            if (context instanceof WindowLockStoreOwner) {
                mWindowLockStoreOwner = (WindowLockStoreOwner) context;
            } else {
                clearActivityWindowLock();
            }
        }

        @Override
        public void onDetach() {
            clearActivityWindowLock();
        }

        //清理Window锁
        private void clearActivityWindowLock() {
            mWindowLockStoreOwner = null;
        }
    };

    @Override
    public WindowLock getWindowLock() {
        if (mWindowLockStoreOwner == null) {
            if (mWindowLock == null) mWindowLock = new WindowLock();
            return mWindowLock;
        } else {
            return mWindowLockStoreOwner.getWindowLock();
        }
    }


}
