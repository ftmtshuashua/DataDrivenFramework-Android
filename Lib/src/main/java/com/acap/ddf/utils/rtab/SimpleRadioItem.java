package com.acap.ddf.utils.rtab;

import android.view.View;
import android.widget.RadioButton;

/**
 * RadioItem简单实现
 * 如果View是RadioButton则通过setChecked()方法改变他的状态
 * 如果是其他View则通过setSelected()方法来改变状态
 */
public class SimpleRadioItem extends RadioItem<View> {

    public SimpleRadioItem(View view) {
        super(view);
    }

    @Override
    public boolean isCheck() {
        View v = getView();
        if (v instanceof RadioButton) {
            return ((RadioButton) v).isChecked();
        } else return v.isSelected();
    }

    @Override
    public boolean onInterceptCheck(boolean isCheck) {
        return isCheck() == isCheck;
    }

    @Override
    public void onChange(View v, boolean isCheck) {
        if (v instanceof RadioButton) {
            ((RadioButton) v).setChecked(isCheck);
        } else v.setSelected(isCheck);
    }
}