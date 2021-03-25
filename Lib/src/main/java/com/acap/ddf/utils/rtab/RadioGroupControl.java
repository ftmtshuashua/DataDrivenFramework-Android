package com.acap.ddf.utils.rtab;

import android.os.Bundle;

import androidx.annotation.IdRes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * <pre>
 * Tip:
 *      RadioGroup控制器
 *
 *      使任何形式的布局实现RadioGroup的效果
 *
 *  Function:
 *      setOnRadioChangeListener()  :设置Radio改变监听器
 *      addRadio()                  :添加Radio项
 *      checkByIndex()              :通过Index选中Radio
 *      checkById()                 :通过Id选中Radio
 *      check()                     :选中Radio
 *      getCheckedRadio()           :获得选中的Radio
 *      getCheckedId()              :获得选中的Radio的资源ID
 *
 *      onRadioChange()             :Radio改变或点击回调
 *
 * Created by LiFuPing on 2018/6/4.
 * </pre>
 */
public class RadioGroupControl {
    /* 被控制Radio孩子集合 */
    private final List<RadioItem> mItemArray = new ArrayList<>();
    /* 当前被选中的Radio */
    private RadioItem mCheckedRadio;
    /*  监听Radio状态改变,在某些时候非常有用 */
    private OnRadioChangeListener mOnCheckedChangeListener;

    /**
     * 设置Radio状态改变监听器,当Radio改变的时候回调
     *
     * @param l 监听器
     */
    public void setOnRadioChangeListener(OnRadioChangeListener l) {
        mOnCheckedChangeListener = l;
    }

    /**
     * 添加一个Radio
     *
     * @param radio the radio
     */
    public final void addRadio(RadioItem radio) {
        mItemArray.add(radio);
        radio.attach(this);
    }

    /**
     * 添加Radio集合
     *
     * @param radioarray radio集合
     */
    public final void addRadio(Collection<? extends RadioItem> radioarray) {
        Iterator<? extends RadioItem> arrays = radioarray.iterator();
        while (arrays.hasNext()) {
            addRadio(arrays.next());
        }
    }

    /**
     * 通过下标来改变选中的Radio
     *
     * @param index radio 下标
     */
    public void checkByIndex(int index) {
        setCheck(mItemArray.get(index));
    }

    /**
     * 遍历Radio View到ID,当找到对应View当时候选中这个View
     *
     * @param id 这个View的资源ID
     */
    public void checkById(@IdRes int id) {
        for (RadioItem radio : mItemArray) {
            if (id == radio.getView().getId()) {
                setCheck(radio);
                return;
            }
        }
        throw new NullPointerException("在RadioGroup中的未找到该ID对应的Radio");
    }

    /**
     * 选中Radio
     *
     * @param radio radio
     */
    public void check(RadioItem radio) {
        setCheck(radio);
    }

    /* 切换逻辑 */
    private void setCheck(RadioItem radio) {
        RadioItem current = mCheckedRadio;

        boolean isCheck = radio.setCheck(true);
        if (isCheck) {
            mCheckedRadio = radio;
            if (current != null && current != radio)
                current.setCheck(false);
            notifyRadioChange(radio);
        } else {
            mCheckedRadio = current;
        }
    }

    /**
     * 获得当前选中的Radio
     *
     * @return 选中的Radio
     */
    public RadioItem getCheckedRadio() {
        return mCheckedRadio;
    }

    /**
     * 获得当前选中Radio的View资源ID
     *
     * @return View资源ID
     */
    @IdRes
    public int getCheckedId() {
        return getCheckedRadio().getView().getId();
    }

    /**
     * 通知状态改变
     *
     * @param radio 当前被选中当radio
     */
    private void notifyRadioChange(RadioItem radio) {
        onRadioChange(radio);
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onRadioChange(radio);
        }
    }

    /**
     * 交给子类实现,当radio改变或者被重复选中当时候调用
     *
     * @param radio 当前被选中当radio
     */
    protected void onRadioChange(RadioItem radio) {

    }


    private static final String KEY_RADIO_STATE = "k-r-s";

    public void onSaveInstanceState(Bundle outState) {
        if (mCheckedRadio == null) {
            int id = mCheckedRadio.getView().getId();
            outState.putInt(KEY_RADIO_STATE, id);
        }
    }

    /**
     * 恢复RadioGroup的状态
     * @param savedInstanceState
     * @return  true:成功恢复 ，false:恢复失败
     */
    public boolean onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int id = savedInstanceState.getInt(KEY_RADIO_STATE, -1);
            if (id != -1) {
                checkById(id);
                return true;
            }
        }
        return false;
    }
}
