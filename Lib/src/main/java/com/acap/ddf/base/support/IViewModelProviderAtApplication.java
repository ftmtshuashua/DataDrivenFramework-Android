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
public interface IViewModelProviderAtApplication {


    /**
     * 获得Class对应的ViewModel实例，生命周期与Application相同
     *
     * @param modelClass ViewModel 的 Class
     * @param <T>
     * @return ViewModel 实例
     */
    <T extends ViewModel> T getViewModelAtApplication(Class<T> modelClass);




}
