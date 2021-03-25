package com.acap.ddf.chain.run;


import com.acap.toolkit.action.Action;
import com.acap.toolkit.thread.ThreadHelper;

/**
 * 运行在Main线程
 *
 * <p>
 * <br/>
 * Author:Hope_LFB<br/>
 * Time:2020/12/10 11:31
 */
public class EventRunMainAction extends EventRunAction<EventRunMainAction> {

    public EventRunMainAction(Action action) {
        super(action);
    }

    @Override
    protected void onRunAction() {
        ThreadHelper.main(() -> super.onRunAction());
    }


}
