package com.acap.ddf.frame;

import android.os.Bundle;

import com.google.gson.reflect.TypeToken;
import com.weather.base.utils.GsonUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <pre>
 * Tip:
 *      框架回退栈
 *
 * Created by ACap on 2021/1/11 14:42
 * </pre>
 */
final class FrameStack {

    private ArrayList<FrameEntry> array = new ArrayList<>();

    //最后一项的下标
    public int indexLast() {
        return array.size() - 1;
    }

    /**
     * 入栈
     */
    public void add(FrameEntry e) {
        array.add(e);
        if (mOnFrameStackChangeListener != null) mOnFrameStackChangeListener.onPush(e);
    }

    /**
     * 查看栈顶信息
     */
    public FrameEntry peek() {
        return array.get(indexLast());
    }

    public FrameEntry peek(int index) {
        return array.get(index);
    }

    /**
     * 栈顶向下第二项的数据
     */
    public FrameEntry two() {
        int index = indexLast() - 1;
        if (index >= 0) {
            return array.get(index);
        }
        return null;
    }

    /**
     * 出栈
     */
    public FrameEntry pop() {
        FrameEntry remove = array.remove(indexLast());
        if (mOnFrameStackChangeListener != null) mOnFrameStackChangeListener.onPop(remove);
        return remove;
    }

    /**
     * 移除栈中数据
     */
    public boolean remove(FrameEntry entry) {
        boolean remove = array.remove(entry);
        if (remove && mOnFrameStackChangeListener != null) mOnFrameStackChangeListener.onPop(entry);
        return remove;
    }

    public int size() {
        return array.size();
    }

    public boolean isEmpty() {
        return array.isEmpty();
    }

    public FrameEntry findFirst(Filter<FrameEntry> filter) {
        for (int i = 0; i < array.size(); i++) {
            FrameEntry e = array.get(i);
            if (filter.filter(e)) {
                return e;
            }
        }
        return null;
    }

    public void clear() {
        array.clear();
    }

    public boolean isDone() {
        for (FrameEntry f_stackEntry : array) {
            if (!f_stackEntry.isDone()) return false;
        }
        return true;
    }

    public interface Filter<E> {
        boolean filter(E e);
    }


    private OnFrameStackChangeListener mOnFrameStackChangeListener;

    public void setOnFrameStackChangeListener(OnFrameStackChangeListener l) {
        l.onInit(array);
        mOnFrameStackChangeListener = l;
    }


    public Iterator<FrameEntry> iterator() {
        return new DequeIterator(array);
    }

    public static class DequeIterator implements Iterator<FrameEntry> {
        private int index;
        private int length;
        private List<FrameEntry> mArray;

        public DequeIterator(List<FrameEntry> array) {
            mArray = new ArrayList<>(array);
            index = 0;
            length = mArray.size();
        }

        @Override
        public boolean hasNext() {
            return index < length;
        }

        @Override
        public FrameEntry next() {
            FrameEntry e = mArray.get(index);
            index++;
            return e;
        }
    }


    public void onRestoreInstanceState(FrameManager manager, Bundle savedInstanceState) {
        List<FrameEntry.StackCache> stack = GsonUtils.get().fromJson(savedInstanceState.getString("StackJson")
                , new TypeToken<List<FrameEntry.StackCache>>() {
                }.getType());
        List<FrameEntry> array = new ArrayList<>();
        for (FrameEntry.StackCache cache : stack) {
            array.add(new FrameEntry(manager, cache));
        }

        //重置Stack数据
        clear();
        array.addAll(array);
        if (mOnFrameStackChangeListener != null) mOnFrameStackChangeListener.onInit(new ArrayList<>(array));
    }

    public void onSaveInstanceState(Bundle outState) {
        List<FrameEntry.StackCache> mCacheArray = new ArrayList<>();
        int size = array.size();
        for (int i = 0; i < size; i++) {
            mCacheArray.add(array.get(i).toCache());
        }
        outState.putString("StackJson", GsonUtils.get().toJson(mCacheArray));
    }


}