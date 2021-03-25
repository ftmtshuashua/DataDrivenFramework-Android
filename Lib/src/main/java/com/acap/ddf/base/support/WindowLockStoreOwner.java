package com.acap.ddf.base.support;

/**
 * 窗口锁的提供者
 * <br/>
 * Author:Hope_LFB<br/>
 * Time:2020/12/10 18:05
 */
public interface WindowLockStoreOwner {
    /**
     * 获得窗口锁
     */
    WindowLock getWindowLock();
}
