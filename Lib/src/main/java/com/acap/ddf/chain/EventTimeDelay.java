package com.acap.ddf.chain;


import com.acap.toolkit.thread.ThreadHelper;

/**
 * 延时事件
 * <p>
 * <br/>
 * Author:Hope_LFB<br/>
 * Time:2020/12/10 11:28
 */
public class EventTimeDelay extends BaseEvent<EventTimeDelay> {
    private long delay;
    private boolean bCallAtIoThread = false;

    /**
     * @param delay 延时时间
     */
    public EventTimeDelay(long delay) {
        this.delay = delay;
    }

    @Override
    protected void call() throws Throwable {
        if (bCallAtIoThread) {
            ThreadHelper.io(() -> {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                }
                next();
            });
        } else {
            ThreadHelper.mainDelayed(() -> next(), delay);
        }
    }

    /**
     * 设置在子线程中回调
     *
     * @param is 是否在子线程中回调
     * @return
     */
    public EventTimeDelay setCallAtIoThread(boolean is) {
        bCallAtIoThread = is;
        return this;
    }

}
