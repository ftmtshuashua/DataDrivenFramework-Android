package com.acap.ddf.chain;

import android.os.Looper;

import com.acap.toolkit.log.LogUtils;
import com.lfp.eventtree.OnEventListener;

import java.text.MessageFormat;


/**
 * 事件耗时检查
 * <p>
 * <br/>
 * Author:Hope_LFB<br/>
 * Time:2020/12/10 13:53
 */
public class OnEventElapsedTimeLog implements OnEventListener {


    private long time_start;
    private String mUserTag;

    public OnEventElapsedTimeLog(String tag) {
        mUserTag = tag;
    }

    @Override
    public void onStart() {
        time_start = System.currentTimeMillis();
    }

    @Override
    public void onNext() {
        LogUtils.fit(getTag(), "{0}({1}) -> {2}", mUserTag, getHs(), "完成");
    }

    @Override
    public void onError(Throwable e) {
        LogUtils.fet(getTag(), "{0}({1}) -> {2}", mUserTag, getHs(), e.getMessage());
    }

    @Override
    public void onComplete() {

    }

    //获得耗时
    private String getHs() {
        return MessageFormat.format("{0,number,0}ms", System.currentTimeMillis() - time_start);
    }

    private String getTag() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return "EventElapsedTime(main)";
        } else return MessageFormat.format("EventElapsedTime({0})", Thread.currentThread().getName());
    }

}
