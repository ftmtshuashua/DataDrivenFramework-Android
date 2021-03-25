package com.acap.ddf.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.acap.toolkit.action.Action;
import com.acap.toolkit.app.ContextUtils;
import com.acap.toolkit.log.LogUtils;
import com.acap.toolkit.thread.ThreadHelper;
import com.lfp.eventtree.EventChain;
import com.lfp.eventtree.OnEventCompleteListener;
import com.lfp.eventtree.OnEventFailureListener;
import com.weather.base.R;
import com.weather.base.utils.BlurUtils;
import com.weather.base.utils.ScreenshotsUtils;

import support.lfp.requestchain.exception.MsgException;

/**
 * Dialog构造器
 * <p>
 * 默认情况
 * 1.全屏显示
 * 2.默认背景使用Activity的截图并加上高斯模糊
 * 3.允许用户通过返回按钮取消ialog
 * 4.不允许用户通过点击背景取消Dialog
 */
public abstract class BaseDialogControl<Builder extends BaseDialogControl, Dialog extends BaseDialog> {
    //是否允许用户手动关闭弹窗
    protected boolean Cancelable;
    //是否允许用户点击弹窗之外的区域时关闭弹窗
    protected boolean CanceledOnTouchOutside;
    //是否全屏显示弹窗
    protected boolean FullScreen;
    //当样式为全屏的时候配置的自定义背景样式
    protected BackgroundProcessor Background;

    //Dialog的布局资源
    protected int LayoutId;
    //Dialog的主题资源
    protected int ThemeResId;

    protected Context context;

    protected Action OnDismissListener;


    public BaseDialogControl(Context context) {
        this.context = context;
        FullScreen = true;
        CanceledOnTouchOutside = false;
        Cancelable = true;
        setBackgroundActivityBlur();
    }

    //主题配置
    public Builder setThemeResId(int themeResId) {
        ThemeResId = themeResId;
        return (Builder) this;
    }

    //是否允许用户关闭Dialog - 包括点击弹窗外关闭和返回按钮关闭
    public Builder setCancelable(boolean cancelable) {
        Cancelable = cancelable;
        return (Builder) this;
    }

    //是否允许用户点击背景来关闭Dialog
    public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        CanceledOnTouchOutside = canceledOnTouchOutside;
        return (Builder) this;
    }

    //设置Dialog全屏样式 - 改样式下可自定义背景
    public Builder setFullScreen(boolean fullScreen) {
        FullScreen = fullScreen;
        return (Builder) this;
    }

    //设置Dialog的内容布局资源
    public Builder setLayoutId(int layoutId) {
        LayoutId = layoutId;
        return (Builder) this;
    }

    //设置背景
    public Builder setBackground(int resId) {
        Background = new BackgroundProcessor() {
            @Override
            public void processor(ImageView imageView) {
                imageView.setImageResource(resId);
            }
        };
        return (Builder) this;
    }

    //设置背景
    public Builder setBackground(Bitmap background) {
        Background = new BackgroundProcessor() {
            @Override
            public void processor(ImageView imageView) {
                imageView.setImageBitmap(background);
            }
        };
        return (Builder) this;
    }

    //设置背景为Activity的模糊样式
    public Builder setBackgroundActivityBlur() {
        Background = new BackgroundProcessor() {

            EventChain mGetActivityScreenshots;
            Bitmap mBlur;

            @Override
            void onShowBegin(Action action) {
                if (mBlur != null) {
                    mBlur.recycle();
                    mBlur = null;
                }
                mGetActivityScreenshots = new GetActivityScreenshots(context)
                        .setOnBlurListener(blur -> mBlur = blur)
                        .addOnEventListener((OnEventFailureListener) e -> LogUtils.e("请求 Activity Blur 图失败", e))
                        .addOnEventListener((OnEventCompleteListener) () -> action.call());
                mGetActivityScreenshots.start();
            }

            @Override
            public void processor(ImageView imageView) {
                if (mBlur != null && !mBlur.isRecycled()) {
                    imageView.setImageBitmap(mBlur);
                } else {
                    imageView.setBackgroundResource(R.color.colorBlack_alp2);
                }
            }

            @Override
            public void onDialogDismiss() {
                mGetActivityScreenshots.interrupt();
                if (mBlur != null) {
                    mBlur.recycle();
                    mBlur = null;
                }
            }
        };
        return (Builder) this;
    }

    //当弹窗关闭时
    public Builder setOnDismissListener(Action onDismissListener) {
        OnDismissListener = onDismissListener;
        return (Builder) this;
    }

    protected abstract Dialog instance(Context context);

    private Dialog mDialog;

    //显示Dialog
    public Dialog show() {
        if (mDialog == null) {
            mDialog = instance(context);
        }
        if (!mDialog.isShowing()) {
            if (Background != null) {
                Background.onShowBegin(() -> mDialog.show());
            } else {
                mDialog.show();
            }
        }
        return mDialog;
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public Context getContext() {
        return context;
    }

    //当Dialog创建时
    protected void onDialogCreate(Dialog dialog) {
    }

    //当Dialog关闭的时候
    protected void onDialogDismiss(Dialog dialog) {
        if (Background != null) Background.onDialogDismiss();
        if (OnDismissListener != null) OnDismissListener.call();
    }


    //全屏背景处理
    public static abstract class BackgroundProcessor {

        //在显示之前是否有需要处理的事件
        void onShowBegin(Action action) {
            action.call();
        }

        //处理背景
        abstract void processor(ImageView imageView);

        //当Dialog关闭的时候
        void onDialogDismiss() {
        }
    }


    //获得Activity截图
    private static final class GetActivityScreenshots extends EventChain {
        Context context;
        OnBlurListener mOnBlurListener;

        public GetActivityScreenshots(Context context) {
            this.context = context;
        }

        @Override
        protected void call() throws Throwable {
            Activity activity = ContextUtils.getActivity(context);
            if (activity == null) {
                error(new MsgException("未获取到Activity实例!"));
                return;
            }

            View viewById = activity.findViewById(android.R.id.content);

            final Bitmap bitmap = ScreenshotsUtils.getView(viewById, Bitmap.Config.ARGB_8888);
            ThreadHelper.io(() -> {
                Bitmap mBlur = BlurUtils.rsBlur(context, bitmap);
                bitmap.recycle();

                ThreadHelper.main(() -> {
                    if (isInterrupt()) {
                        if (mBlur != null && !mBlur.isRecycled()) {
                            mBlur.recycle();
                        }
                    } else {
                        if (mOnBlurListener != null) {
                            mOnBlurListener.onBlur(mBlur);
                        }
                    }
                    next();
                });
            });
        }

        public GetActivityScreenshots setOnBlurListener(OnBlurListener mOnBlurListener) {
            this.mOnBlurListener = mOnBlurListener;
            return this;
        }

        public interface OnBlurListener {
            void onBlur(Bitmap blur);
        }

    }


    //--------- 生命周期绑定 ----------

//    private void init() {
//        Activity activity = ContextTo.INSTANCE.activity(context);
//        if (activity == null) return;
//        if (activity instanceof LifecycleOwner) {
//            ((LifecycleOwner) activity).getLifecycle()
//        }
//    }

}
