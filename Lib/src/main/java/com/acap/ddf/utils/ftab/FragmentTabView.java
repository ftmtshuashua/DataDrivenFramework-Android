package com.acap.ddf.utils.ftab;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.view.AbsSavedState;

import com.acap.ddf.widget.FitsSystemGroupFrameLayout;


/**
 * 管理首页Fragment切换管理
 */
public class FragmentTabView extends FitsSystemGroupFrameLayout {

    private static final int ID_UNKONE = -1;

    public FragmentTabView(Context context) {
        super(context);
    }

    public FragmentTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FragmentTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //适配器
    private FragmentTabAdapter mFragmentTabAdapter;
    private int mCurTabId = ID_UNKONE; //当前Item的ID

    //设置当前Item

    /**
     * 设置当前页面
     *
     * @param id 页面对应的ID
     */
    public void setCurrentItem(int id) {
        if (id == ID_UNKONE) throw new IllegalStateException("id不能等于-1!");
        if (mFragmentTabAdapter != null && mCurTabId != id) {
            int id_hint = mCurTabId;
            mCurTabId = id;
            mFragmentTabAdapter.setFragmentShow(this, id);
            if (id_hint != ID_UNKONE) mFragmentTabAdapter.setFragmentHint(this, id_hint);
            mFragmentTabAdapter.finishUpdate(this);
        }
    }

    public void setAdapter(FragmentTabAdapter adapter) {
        mFragmentTabAdapter = adapter;
    }


    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.cur_id = mCurTabId;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mCurTabId = ss.cur_id;
    }

    //状态储存
    static class SavedState extends AbsSavedState {
        int cur_id;
        Parcelable adapterState;
        ClassLoader loader;

        public SavedState(@NonNull Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(cur_id);
            out.writeParcelable(adapterState, flags);
        }

        @Override
        public String toString() {
            return "FragmentPager.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " id=" + cur_id + "}";
        }

        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            cur_id = in.readInt();
            adapterState = in.readParcelable(loader);
            this.loader = loader;
        }
    }
}
