package com.acap.ddf.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.weather.base.base.support.IWeatherApplication;

/**
 *
 * <p>
 * Author:Hope_LFB
 * Time:2020/12/1 11:05
 */
public class BaseApplication extends Application implements IWeatherApplication {

    private ViewModelStore mViewModelStore;
    private ViewModelProvider mVMP_Application;
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    @Override
    public void onCreate() {
        super.onCreate();
        mLifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
        mLifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
        mLifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mLifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        if (mViewModelStore == null) mViewModelStore = new ViewModelStore();
        return mViewModelStore;
    }

    @Override
    public <T extends ViewModel> T getViewModelAtApplication(Class<T> modelClass) {
        if (mVMP_Application == null) mVMP_Application = new ViewModelProvider(this);
        return mVMP_Application.get(modelClass);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}
