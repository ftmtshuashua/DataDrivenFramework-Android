package com.acap.ddf.model.utils;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.HashMap;

/**
 * <pre>
 * Tip:
 *      帮助管理LiveModel的监听
 *
 * Created by ACap on 2021/1/25 14:15
 * </pre>
 */
public class LiveModelHelper {

    private final LifecycleOwner mOwner;
    private final HashMap<Observer, LiveData> mOnlyObserve = new HashMap<>();


    public LiveModelHelper(LifecycleOwner owner) {
        this.mOwner = owner;
    }

    /**
     * 使Observer绑定唯一的LiveData
     *
     * @param liveData
     * @param observer
     */
    public void observeOnly(LiveData liveData, Observer observer) {
        LiveData save = mOnlyObserve.get(observer);
        if (save == liveData) return;

        if (save != null) {
            LiveData remove = mOnlyObserve.remove(observer);
            remove.removeObserver(observer);
        }

        if (liveData != null) {
            liveData.observe(mOwner, observer);
            mOnlyObserve.put(observer, liveData);
        }
    }


}
