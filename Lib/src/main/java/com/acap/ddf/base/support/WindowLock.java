package com.acap.ddf.base.support;

import androidx.lifecycle.LifecycleObserver;

import com.acap.toolkit.action.Action1;
import com.acap.toolkit.thread.ThreadHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 窗口锁
 * <p>
 * 用于保证窗口中正在执行事件的唯一性
 */
public class WindowLock implements LifecycleObserver {

    private Object RunLock = new Object();
    private Lock mLock;

    //锁的竞争者
    private List<Action1<Lock>> mCompetitors;

    /**
     * 执行逻辑代码
     */
    public void run(Action1<Lock> action) {
        synchronized (RunLock) {
            if (mLock != null) {
                if (mCompetitors == null) mCompetitors = new ArrayList<>();
                mCompetitors.add(action);
            } else {
                mLock = new Lock(this, action);
                mLock.run();
            }
        }
    }

    /**
     * 释放锁
     */
    private void release() {
        Action1<Lock> next = null;
        synchronized (RunLock) {
            if (mLock != null) {
                mLock.mWindow = null;
                mLock = null;
            }
            if (mCompetitors != null && !mCompetitors.isEmpty()) {
                next = mCompetitors.remove(0);
            }
        }
        if (next != null) {
            run(next);
        }
    }


    //窗口的锁对象
    public static final class Lock {
        private WindowLock mWindow;
        private Action1<Lock> mAction;
        private boolean isRuning = false;

        public Lock(WindowLock windowLock, Action1<Lock> action) {
            mWindow = windowLock;
            mAction = action;
        }

        //释放这把锁
        public void release() {
            if (mWindow != null) mWindow.release();
        }

        private void run() {
            if (isRuning) return;
            isRuning = true;
            if (mWindow != null) {
                ThreadHelper.main(() -> mAction.call(Lock.this));
            }
        }

    }


}
