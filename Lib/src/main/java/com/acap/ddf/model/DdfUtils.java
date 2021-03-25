package com.acap.ddf.model;

import android.os.Looper;


/**
 * <pre>
 * Tip:
 *      常用工具方法集合
 * Function:
 *      isEmpty()       :判断是否为空或者空数据
 *      isNotEmpty()    :判断非空并且非空数据
 *      checkNotNull()  :检查对象是否为空
 *      equals()        :比较两个对象是否相等
 *      isMainThread()  :判断是否为主线程
 *
 * Created by ACap on 2018/5/30.
 * </pre>
 */
public class DdfUtils {

    /**
     * @return 如果当前线程是主线程则返回true
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }


}
