package com.acap.ddf.chain.run;


import com.acap.toolkit.action.Action;
import com.weather.base.chain.BaseEvent;

/**
 * 该事件执行一个Action
 * <p>
 * <br/>
 * Author:Hope_LFB<br/>
 * Time:2020/12/10 11:46
 */
public class EventRunAction<T extends EventRunAction> extends BaseEvent<T> {

    private Action action;

    public EventRunAction(Action action) {
        this.action = action;
    }

    @Override
    protected void call() throws Throwable {
        onRunAction();
    }

    /**
     * Action 的执行逻辑
     */
    protected void onRunAction() {
        try {
            if (action != null) {
                action.call();
            }
            next();
        } catch (Exception e) {
            error(e);
        }
    }


}
