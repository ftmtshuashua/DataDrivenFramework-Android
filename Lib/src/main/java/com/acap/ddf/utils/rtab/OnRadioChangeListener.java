package com.acap.ddf.utils.rtab;

/**
 * <pre>
 * Tip:
 * Radio切换监听
 *
 * Created by ACap on 2021/1/22 18:31
 * </pre>
 */
public interface OnRadioChangeListener<T extends RadioItem> {

    void onRadioChange(T radio);
}