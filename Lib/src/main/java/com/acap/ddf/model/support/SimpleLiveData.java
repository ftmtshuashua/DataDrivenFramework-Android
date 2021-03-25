package com.acap.ddf.model.support;

import androidx.lifecycle.MutableLiveData;

import com.acap.ddf.model.DdfUtils;

/**
 * <pre>
 * Tip:
 *      自动根据线程分配提交方式
 *
 * Created by ACap on 2021/1/21 14:04
 * </pre>
 */
public class SimpleLiveData<T> extends MutableLiveData<T> {
    public SimpleLiveData(T value) {
        super(value);
    }

    public SimpleLiveData() {
    }

    @Override
    public void postValue(T value) {
        if (DdfUtils.isMainThread()) {
            super.setValue(value);
        } else {
            super.postValue(value);
        }
    }

    @Override
    public void setValue(T value) {
        if (DdfUtils.isMainThread()) {
            super.setValue(value);
        } else {
            super.postValue(value);
        }
    }
}
