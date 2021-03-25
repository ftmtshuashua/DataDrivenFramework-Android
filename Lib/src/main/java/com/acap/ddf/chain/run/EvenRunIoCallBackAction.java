package com.acap.ddf.chain.run;


import com.acap.toolkit.action.Action1;
import com.acap.toolkit.thread.ThreadHelper;

/**
 * <br/>
 * Author:Hope_LFB<br/>
 * Time:2020/12/17 18:45
 */
public class EvenRunIoCallBackAction extends EvenRunCallBackAction<EvenRunIoCallBackAction> {

    public EvenRunIoCallBackAction(Action1<CallBack> action) {
        super(action);
    }

    @Override
    protected void call() throws Throwable {
        ThreadHelper.io(() -> {
            try {
                super.call();
            } catch (Throwable e) {
                error(e);
            }
        });
    }
}
