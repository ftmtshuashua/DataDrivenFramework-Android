package com.acap.ddf.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.weather.base.base.SingleFragment;
import com.weather.base.frame.FrameEntry;
import com.weather.base.frame.FrameEntryContainer;
import com.weather.base.frame.FrameManager;
import com.weather.base.frame.IFrameProvider;

/**
 * <pre>
 * Tip:
 *      单Activity架构中的Activity
 *
 * Created by ACap on 2021/1/7 15:52
 * </pre>
 */
public class SingleActivity extends AppCompatActivity implements IFrameProvider {

    private FrameManager mFrameManager = new FrameManager(this);

    public FrameManager getFrameManager() {
        /*if (mFrameManager == null) {
            mFrameManager = new FrameManager(this);
        }*/
        return mFrameManager;
    }

    /**
     * 获得当前处于栈顶的Fragment
     */
    public Fragment getTopFragment() {
        return getFrameManager().getTopFragment();
    }

    //容器配置
    protected void setFrameContainer(FrameEntryContainer container) {
        getFrameManager().setContainer(container);
    }

    /**
     * 配置用于显示Fragment的Activity和承载Fragment的FrameLayout
     */
    protected void setFrameInit(Bundle savedInstanceState) {
        getFrameManager().onActivityCreate(getIntent(), savedInstanceState);
    }

    //设置入口Fragment
    protected void setFrameStart(Class<? extends BaseFragment> fcls) {
        setFrameStart(fcls, null);
    }

    //设置入口Fragment
    protected void setFrameStart(Class<? extends BaseFragment> fcls, Bundle args) {
        if (getFrameManager().size() == 0) {
            FrameEntry frameEntry = new FrameEntry(getFrameManager(), fcls, args);
            frameEntry.setAnim(SingleFragment.ANIM_NONE);
            getFrameManager().toFragment(frameEntry);
        }
    }

    @Override
    public void toFragment(Class<? extends BaseFragment> fcls) {
        getFrameManager().toFragment(fcls);
    }

    @Override
    public void toFragment(Class<? extends BaseFragment> fcls, Bundle args) {
        getFrameManager().toFragment(fcls, args);
    }

    @Override
    public void toFragment(FrameEntry entry) {
        getFrameManager().toFragment(entry);
    }

    @Override
    public void toFragmentForResult(Class<? extends BaseFragment> fcls, int requestCode) {
        getFrameManager().toFragmentForResult(fcls, requestCode);
    }

    @Override
    public void toFragmentForResult(Class<? extends BaseFragment> fcls, Bundle args, int requestCode) {
        getFrameManager().toFragmentForResult(fcls, args, requestCode);
    }

    @Override
    public void toFragmentForResult(FrameEntry entry, int requestCode) {
        getFrameManager().toFragmentForResult(entry, requestCode);

    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getFrameManager().onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getFrameManager().onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getFrameManager().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getFrameManager().onKeyDown(keyCode, event)) return true;
        return super.onKeyDown(keyCode, event);
    }

}
