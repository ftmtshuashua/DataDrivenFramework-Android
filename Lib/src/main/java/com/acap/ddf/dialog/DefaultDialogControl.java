package com.acap.ddf.dialog;

import android.content.Context;

/**
 * 使用默认的 BaseDialog2 来创建Dialog
 *
 * @param <Control>
 */
public class DefaultDialogControl<Control extends DefaultDialogControl> extends BaseDialogControl<Control, BaseDialog> {

    public DefaultDialogControl(Context context) {
        super(context);
    }

    @Override
    protected BaseDialog instance(Context context) {
        return new BaseDialog(context, this);
    }
}
