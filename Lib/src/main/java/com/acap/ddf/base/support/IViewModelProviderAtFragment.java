package com.acap.ddf.base.support;

import androidx.lifecycle.ViewModel;

/**
 * ViewModel提供者
 *
 * <p>
 * <br/>
 * Author:Hope_LFB<br/>
 * Time:2020/12/8 15:35
 */
public interface IViewModelProviderAtFragment extends IViewModelProvider {


    /**
     * 获得Class对应的ViewModel实例，生命周期与Fragment相同
     *
     * @param modelClass ViewModel 的 Class
     * @param <T>
     * @return ViewModel 实例
     */
    <T extends ViewModel> T getViewModelAtFragment(Class<T> modelClass);


}
