package com.acap.ddf.utils.ftab;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.acap.ddf.base.SingleFragment;


public abstract class FragmentTabAdapter {

    private FragmentManager mManager;

    public FragmentTabAdapter(FragmentManager manager) {
        this.mManager = manager;
    }

    private FragmentTransaction mCurrTransaction;

    private FragmentTransaction getTransaction() {
        if (mCurrTransaction == null) {
            mCurrTransaction = mManager.beginTransaction();
        }
        return mCurrTransaction;
    }

    /**
     * 生成ID对应的Fragment
     *
     * @param id Fragment的ID
     * @return Fragment
     */
    public abstract Fragment newInstance(int id);


    public void setFragmentHint(@NonNull ViewGroup container, int id) {
        String tag = makeFragmentTag(container.getId(), id);
        Fragment fragment = mManager.findFragmentByTag(tag);
        if (fragment != null) {
            getTransaction().hide(fragment);
            fragment.setMenuVisibility(false);
            getTransaction().setMaxLifecycle(fragment, Lifecycle.State.STARTED);
        }
    }

    public void setFragmentShow(@NonNull ViewGroup container, int id) {
        String tag = makeFragmentTag(container.getId(), id);
        Fragment fragment = mManager.findFragmentByTag(tag);
        boolean isAdded = fragment != null;

        if (!isAdded) fragment = newInstance(id);

        if (fragment instanceof SingleFragment) {
            int[] anim = ((SingleFragment) fragment).getTransAnim();
            if (anim == null) anim = ((SingleFragment) fragment).getTransAnim();
            getTransaction().setCustomAnimations(anim[0], anim[1], anim[2], anim[3]);
        }

        if (!isAdded) getTransaction().add(container.getId(), fragment, tag);
        else getTransaction().show(fragment);

        fragment.setMenuVisibility(true);
        getTransaction().setMaxLifecycle(fragment, Lifecycle.State.RESUMED);

    }

    public void finishUpdate(@NonNull ViewGroup container) {
        if (mCurrTransaction != null) {
            mCurrTransaction.setReorderingAllowed(true);
            mCurrTransaction.commitNowAllowingStateLoss();
            mCurrTransaction = null;
        }
    }

    //生成Fragment的Tag
    static String makeFragmentTag(int viewId, long id) {
        return "fragment-tab:" + viewId + ":" + id;
    }


}
