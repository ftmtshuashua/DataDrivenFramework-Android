package com.acap.ddf.utils.rtab;

import android.view.View;

/**
 * RadioItem 在任何View外套一个的壳。以实现类似RadioGroup的效果
 * <p>
 * 简单实现可以使用{@link SimpleRadioItem}
 * <p>
 * Created by ACap on 2021/1/22 18:27
 *
 * @param <V> extends View
 */
public abstract class RadioItem<V extends View> implements View.OnClickListener {
    RadioGroupControl mControl;
    V mView;


    public RadioItem(V view) {
        mView = view;
        view.setOnClickListener(this);
    }

    /*关联RadioGroupControl*/
    protected void attach(RadioGroupControl control) {
        mControl = control;
    }

    /**
     * 被Radio包裹控制的View
     *
     * @return V
     */
    public V getView() {
        return mView;
    }

    /**
     * 拦截点击事件，在一些特殊情况下通过拦截事件来阻止RadioGroup切换选中状态
     *
     * @param check boolean
     * @return boolean
     */
    public boolean onInterceptCheck(boolean check) {
        return false;
    }

    /**
     * 准备改变这个Raido的选中状态
     *
     * @param check 准备改变到 isCheck 状态
     * @return 改变之后的选中状态
     */
    protected boolean setCheck(boolean check) {
        boolean isIntercept = onInterceptCheck(check);
        if (!isIntercept) {
            onChange(getView(), check);
        }
        return isCheck();
    }

    /**
     * 获得这个radio的选中状态
     * 在一些特殊场景,如被内存被回收之后还能获得正确当状态
     *
     * @return 当前选中状态
     */
    public abstract boolean isCheck();

    /**
     * 根据根据最终结果改变View显示状态
     *
     * @param v     被radio包裹当View
     * @param check 准备改变为当选中状态
     */
    public abstract void onChange(V v, boolean check);

    @Override
    public void onClick(View v) {
        if (v == mView) mControl.check(this);
    }
}
