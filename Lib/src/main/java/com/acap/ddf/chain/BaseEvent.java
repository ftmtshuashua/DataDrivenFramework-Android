package com.acap.ddf.chain;

import com.lfp.eventtree.EventChain;

/**
 * 提供一些常用的基础功能
 * <p>
 * <br/>
 * Author:Hope_LFB<br/>
 * Time:2020/12/10 14:07
 */
public abstract class BaseEvent<T extends BaseEvent> extends EventChain {

    /**
     * 设置输出事件执行的耗时日志
     *
     * ET:ElapsedTime
     *
     * @param tag 日志的TAG
     * @return
     */
    public T setPrintET(String tag) {
        addOnEventListener(new OnEventElapsedTimeLog(tag));
        return (T) this;
    }

}
