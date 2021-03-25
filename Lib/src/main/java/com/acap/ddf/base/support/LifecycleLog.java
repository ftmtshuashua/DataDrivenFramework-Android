package com.acap.ddf.base.support;


import com.acap.toolkit.Utils;
import com.acap.toolkit.log.LogUtils;

import java.text.MessageFormat;

/**
 * 生命周期日志
 * <p>
 * <br/>
 * Author:Hope_LFB<br/>
 * Time:2020/12/8 15:25
 */
public class LifecycleLog {

    public static final void log(Object obj, String msg) {
        if (obj == null) {
            LogUtils.i("Lifecycle", msg);
        } else {
            LogUtils.fit("Lifecycle", "{0}({1}) -> {2}", obj.getClass().getSimpleName(), Utils.getObjectId(obj), msg);
        }
    }
}
