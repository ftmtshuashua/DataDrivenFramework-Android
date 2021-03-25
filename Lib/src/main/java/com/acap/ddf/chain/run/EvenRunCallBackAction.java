package com.acap.ddf.chain.run;


import com.acap.toolkit.action.Action1;
import com.weather.base.chain.BaseEvent;

/**
 * <br/>
 * Author:Hope_LFB<br/>
 * Time:2020/12/17 18:40
 */
public class EvenRunCallBackAction<T extends EvenRunCallBackAction> extends BaseEvent<T> {

    private Action1<CallBack> action;

    public EvenRunCallBackAction(Action1<CallBack> action) {
        this.action = action;
    }

    @Override
    protected void call() throws Throwable {
        CallBack callBack = new CallBack() {
            @Override
            public void next() {
                EvenRunCallBackAction.this.next();
            }

            @Override
            public void error(Throwable e) {
                EvenRunCallBackAction.this.error(e);
            }
        };
        if (action != null) {
            action.call(callBack);
        } else {
            callBack.next();
        }
    }

    //回调函数
    public interface CallBack {
        void next();

        void error(Throwable e);
    }
}
