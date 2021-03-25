package com.acap.ddf.frame;

import android.os.Bundle;

import com.weather.base.base.BaseFragment;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/1/18 10:28
 * </pre>
 */
public interface IFrameProvider {
    /**
     * 场景转换
     *
     * @param fcls 目标Fragment
     */
    void toFragment(Class<? extends BaseFragment> fcls);

    /**
     * 场景转换
     *
     * @param fcls 目标Fragment
     * @param args 默认参数
     */
    void toFragment(Class<? extends BaseFragment> fcls, Bundle args);

    /**
     * 场景转换
     */
    void toFragment(FrameEntry entry);

    /**
     * 回调形式场景转换
     */
    void toFragmentForResult(Class<? extends BaseFragment> fcls, int requestCode);

    /**
     * 回调形式场景转换
     */
    void toFragmentForResult(Class<? extends BaseFragment> fcls, Bundle args, int requestCode);

    /**
     * 回调形式场景转换
     */
    void toFragmentForResult(FrameEntry entry, int requestCode);
}
