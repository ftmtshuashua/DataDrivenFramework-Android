package com.acap.ddf.frame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.acap.toolkit.log.LogUtils;
import com.weather.base.base.BaseFragment;
import com.weather.base.base.SingleFragment;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * <pre>
 * Tip:
 *      场景转换
 *
 * Created by ACap on 2021/1/6 17:51
 * </pre>
 */
public class FrameManager implements IFrameProvider {

    private static final String TAG = "FrameManager";
    public static final String KEY_FRAGMENT = "k-fmt";
    public static final String KEY_BUNDLE = "k-fmtb";
    public static final String KEY_REQUEST_CODE = "k-fmtrc";
    public static final int ACTIVITY_REQUEST_CODE = 0x8000; //转场

    private AppCompatActivity mActivity;
    private FragmentManager mFragmentManager;
    private final FrameStack mBackStack = new FrameStack();      //回退栈

    private final RestoreFrame mRestore = new RestoreFrame(this, () -> synchronous());
    private FrameEntryContainer mContainer = FrameEntryContainer.DEFAULT;

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    public FragmentManager getFragmentManager() {
        return mFragmentManager;
    }

    /**
     * 配置容器
     */
    public void setContainer(FrameEntryContainer container) {
        mContainer = container;
    }

    public FrameManager(Activity activity) {
        if (!(activity instanceof AppCompatActivity)) {
            throw new IllegalStateException(MessageFormat.format("{0} 请继承于 androidx.appcompat.app.AppCompatActivity", activity.getClass().getName()));
        }
        mActivity = (AppCompatActivity) activity;
        mFragmentManager = mActivity.getSupportFragmentManager();
        mActivity.getLifecycle().addObserver(mRestore);
    }

    @Override
    public void toFragment(Class<? extends BaseFragment> fcls) {
        toFragment(fcls, null);
    }

    @Override
    public void toFragment(Class<? extends BaseFragment> fcls, Bundle args) {
        FrameEntry frameEntry = new FrameEntry(this, fcls, args);
        frameEntry.setContainer(mContainer);
        toFragment(frameEntry);
    }

    @Override
    public void toFragmentForResult(Class<? extends BaseFragment> fcls, int requestCode) {
        toFragmentForResult(fcls, null, requestCode);
    }

    @Override
    public void toFragmentForResult(Class<? extends BaseFragment> fcls, Bundle args, int requestCode) {
        FrameEntry frameEntry = new FrameEntry(this, fcls, args);
        frameEntry.setContainer(mContainer);
        toFragmentForResult(frameEntry, requestCode);
    }

    @Override
    public void toFragmentForResult(FrameEntry entry, int requestCode) {
        entry.setRequestCode(requestCode);
        toFragment(entry);
    }

    @Override
    public void toFragment(FrameEntry entry) {
        int flag = entry.getFlag();
        if ((flag & FrameEntry.FLAG_CONTAINER_USE) == 0) {
            entry.setContainer(mContainer);
        }
        Class<? extends Activity> containerClass = entry.getContainerClass();
        if (containerClass == mActivity.getClass()) {
            mBackStack.add(entry);
            synchronous();
        } else {
            Intent intent = new Intent(mActivity, containerClass);
            intent.putExtra(KEY_FRAGMENT, entry.getFragmentClass().getName());
            intent.putExtra(KEY_BUNDLE, entry.getFragmentBundle());
            intent.putExtra(KEY_REQUEST_CODE, entry.getRequestCode());
            mActivity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
        }
    }

    //栈信息同步
    private synchronized void synchronous() {
        if (mBackStack.isDone()) return;
        if (isStateSaved()) return;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Iterator<FrameEntry> iterator = mBackStack.iterator();
        FrameEntry prev = null;
        while (iterator.hasNext()) {
            FrameEntry next = iterator.next();
            int action = next.getAction();
            if (action == FrameEntry.ACTION_ADD) {
                ft_add(ft, next, prev);
            } else if (action == FrameEntry.ACTION_REMOVE) {
                ft_pop(ft, next, prev);
                mBackStack.remove(next);
            }
            next.setAction(FrameEntry.ACTION_DONE);
            prev = next;
        }
        ft.setReorderingAllowed(true);
        ft.commit();

    }

    private void ft_add(FragmentTransaction ft, FrameEntry add, FrameEntry pause) {
        Fragment fragment = add.getFragment();
        String tag = add.getTag();
        int layout = add.getContainerLayout();
        if (fragment instanceof SingleFragment) {
            int[] anim = add.getAnim();
            if (anim == null) anim = ((SingleFragment) fragment).getTransAnim();
            ft.setCustomAnimations(anim[0], anim[1], anim[2], anim[3]);
        }
        ft.add(layout, fragment, tag);

        /*if (pause != null) { //A动画生效
            ft.hide(pause.getFragment());
            ft.replace(layout, fragment, tag);
        }*/

//        ft.addToBackStack(tag);
//        ft.addSharedElement(sharedElement.getKey(), sharedElement.getValue());//API 21+
        if (pause != null) {
            Fragment f_pause = pause.getFragment();
            f_pause.setMenuVisibility(false);
            ft.setMaxLifecycle(f_pause, Lifecycle.State.STARTED);
        }
    }

    private void ft_pop(FragmentTransaction ft, FrameEntry pop, FrameEntry resume) {
        Fragment fragment = pop.getFragment();
        boolean isrt = !mBackStack.isEmpty() && pop == mBackStack.peek();
        if (isrt) {
            if (fragment instanceof SingleFragment) {
                int[] anim = pop.getAnim();
                if (anim == null) anim = ((SingleFragment) fragment).getTransAnim();
                ft.setCustomAnimations(anim[2], anim[3], anim[0], anim[1]);
            }
            if (resume != null) {
                Fragment f_resume = resume.getFragment();
                f_resume.setMenuVisibility(true);
                ft.setMaxLifecycle(f_resume, Lifecycle.State.RESUMED);

                int requestCode = pop.getRequestCode();
                if (requestCode > 0 && f_resume instanceof SingleFragment && fragment instanceof SingleFragment) { //回复内容
                    SingleFragment sf_pop = (SingleFragment) fragment;
                    SingleFragment sf_resume = (SingleFragment) f_resume;
                    sf_resume.setFragmentResult(requestCode, sf_pop.getResultCode(), sf_pop.getResultData());
                }
            }
        }
        ft.remove(fragment);

    }

    public boolean isStateSaved() {
        boolean stateSaved = mFragmentManager.isStateSaved();
        if (stateSaved) {
            LogUtils.i(TAG, "忽略: FragmentManager已经保存了它的状态!");
        }
        return stateSaved;
    }

    public void finish(Fragment fragment) {
        FrameEntry f_stack = findByFragment(fragment);
        if (f_stack != null) {
            if (size() <= 1) {
                int requestCode = f_stack.getRequestCode();
                if (requestCode > 0 && f_stack.getFragment() instanceof SingleFragment) {
                    SingleFragment fmt = (SingleFragment) f_stack.getFragment();
                    int resultCode = fmt.getResultCode();
                    Intent resultData = fmt.getResultData();
                    if (resultData == null) resultData = new Intent();
                    resultData.putExtra(KEY_REQUEST_CODE, requestCode);
                    mActivity.setResult(resultCode, resultData);
                }
                mActivity.finish();
            } else {
                f_stack.setAction(FrameEntry.ACTION_REMOVE);
                synchronous();
            }
        } else {
            LogUtils.fet(TAG, "{0} -> 已出栈 或 未入栈", fragment);
        }
    }

    //从栈中找到Entry
    private FrameEntry findByFragment(Fragment fragment) {
        return mBackStack.findFirst(entry -> entry.getTag().equals(fragment.getTag()));
    }

    private FrameEntry getTopEntry() {
        if (mBackStack.isEmpty()) return null;
        return mBackStack.peek();
    }

    /**
     * 获得当前顶部的Fragment
     */
    public Fragment getTopFragment() {
        FrameEntry top = getTopEntry();
        if (top == null) return null;
        return top.getFragment();
//        return mFragmentManager.findFragmentByTag(top.getTag());
    }

    /**
     * 获得栈中Fragment的数量
     */
    public int size() {
        return mBackStack.size();
    }


    private FrameStackChangeManager mFrameStackChangeManager;

    public FrameStackChangeManager getFrameStackChangeManager() {
        if (mFrameStackChangeManager == null) {
            mFrameStackChangeManager = new FrameStackChangeManager();
            mBackStack.setOnFrameStackChangeListener(mFrameStackChangeManager);
        }
        return mFrameStackChangeManager;
    }

    public static final class FrameStackChangeManager implements OnFrameStackChangeListener {

        private List<OnFrameStackChangeListener> array;

        private List<FrameEntry> mArrayCache = new ArrayList<>();

        @Override
        public void onInit(List<FrameEntry> entrys) {
            mArrayCache.clear();
            mArrayCache.addAll(entrys);
            if (array == null) return;
            for (int i = 0; i < array.size(); i++) {
                array.get(i).onInit(mArrayCache);
            }
        }

        @Override
        public void onPush(FrameEntry entry) {
            mArrayCache.add(entry);
            if (array == null) return;
            for (int i = 0; i < array.size(); i++) {
                array.get(i).onPush(entry);
            }
        }

        @Override
        public void onPop(FrameEntry entry) {
            mArrayCache.remove(entry);
            if (array == null) return;
            for (int i = 0; i < array.size(); i++) {
                array.get(i).onPop(entry);
            }
        }

        public void registerOnStackChangeListener(OnFrameStackChangeListener l) {
            if (array == null) array = new ArrayList<>();
            array.add(l);
            l.onInit(mArrayCache);
        }

        public void unregisterOnStackChangeListener(OnFrameStackChangeListener l) {
            if (array == null) return;
            array.remove(l);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ACTIVITY_REQUEST_CODE && data != null) { //来自于内部转场逻辑
            int fmt_requestcode = data.getIntExtra(KEY_REQUEST_CODE, -1);
            if (fmt_requestcode >= 0) { //
                Fragment topFragment = getTopFragment();
                if (topFragment != null && topFragment instanceof SingleFragment) {
                    topFragment.onActivityResult(fmt_requestcode, resultCode, data);
                }
            }
        }
    }

    /**
     * 当Activity启动时
     */
    public void onActivityCreate(Intent intent, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mBackStack.onRestoreInstanceState(this, savedInstanceState);
        }

        if (intent != null && size() == 0) {
            String fragment_clas = intent.getStringExtra(KEY_FRAGMENT);
            if (TextUtils.isEmpty(fragment_clas)) return;
            FrameEntry entry = new FrameEntry(this, fragment_clas, intent.getBundleExtra(KEY_BUNDLE));

            int requestcode = intent.getIntExtra(KEY_REQUEST_CODE, 0);
            if (requestcode != 0) entry.setRequestCode(requestcode);

            entry.setAnim(SingleFragment.ANIM_NONE);
            toFragment(entry);
//            fragment.setInitialSavedState();
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public void onSaveInstanceState(Bundle outState) {
        mBackStack.onSaveInstanceState(outState);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Fragment topFragment = getTopFragment();
        if (topFragment != null && topFragment instanceof SingleFragment && ((SingleFragment) topFragment).onKeyDown(keyCode, event)) {
            return true;
        }
        return false;
    }

}
