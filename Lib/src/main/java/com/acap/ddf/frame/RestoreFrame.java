package com.acap.ddf.frame;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.acap.toolkit.action.Action;
import com.acap.toolkit.log.LogUtils;

/**
 * <pre>
 * Tip:
 *      当某些情况下导致Fragment的动作无法正确执行时候，等待一个时机来执行该动作
 *
 * Created by ACap on 2021/1/15 17:38
 * </pre>
 */
class RestoreFrame implements LifecycleEventObserver {
    //标记Activity是否正在活动
    private boolean mIsAreActivities = false;
    private Action mOnRestore;
    private FrameManager mManager;


    public RestoreFrame(FrameManager manager, Action OnRestore) {
        mManager = manager;
        mOnRestore = OnRestore;
    }

    private void setAreActivities(boolean isAreActivities) {
        if (mIsAreActivities != isAreActivities) {
            if (isAreActivities) {
                if (!mManager.isStateSaved()) {
                    mIsAreActivities = true;
                    onIsAreActivitiesChanged();
                }
            } else {
                mIsAreActivities = false;
                onIsAreActivitiesChanged();
            }
        }
    }

    private void onIsAreActivitiesChanged() {
        LogUtils.fit("前后台状态监听", "前台：{0}", mIsAreActivities);
        if (mIsAreActivities && mOnRestore != null) mOnRestore.call();
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_ANY || event == Lifecycle.Event.ON_CREATE) return;
        setAreActivities(event.ordinal() <= Lifecycle.Event.ON_PAUSE.ordinal());
    }
}
