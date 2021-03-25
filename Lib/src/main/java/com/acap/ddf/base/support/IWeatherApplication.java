package com.acap.ddf.base.support;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;

import com.weather.base.base.support.IViewModelProviderAtApplication;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/1/13 18:46
 * </pre>
 */
public interface IWeatherApplication extends ViewModelStoreOwner, IViewModelProviderAtApplication, LifecycleOwner {


}
