package com.acap.ddf.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.acap.toolkit.app.ContextUtils;
import com.weather.base.R;
import com.weather.base.utils.ActivityUtils;

/**
 * 弹窗基类
 */
public class BaseDialog extends Dialog {

    public BaseDialog(@NonNull Context context, BaseDialogControl builder) {
        super(context, builder.ThemeResId != 0 ? builder.ThemeResId : (builder.FullScreen ? R.style.FullScreenDialogStyle : R.style.BaseDialogStyle));
        mDialogBuilder = builder;
    }

    /**
     * 标记是否允许用户手动取消弹窗
     */
    private boolean mCancelable = true;
    private boolean mCanceledOnTouchOutside = true;

    private BaseDialogControl mDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDialogBuilder == null) return;

        if (mDialogBuilder.FullScreen) {
            initFullScreenDialog();
        } else {
            setContentView(mDialogBuilder.LayoutId);
        }
        mDialogBuilder.onDialogCreate(this);

        setOnDismissListener(dialog -> mDialogBuilder.onDialogDismiss(this));
    }


    @Override
    public void show() {
        if (ActivityUtils.isActivityAlive(getContext())) {
            super.show();
        }
    }

    @Override
    public void dismiss() {
        if (ActivityUtils.isActivityAlive(getContext())) {
            super.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mDialogBuilder == null) return;
        setCancelable(mDialogBuilder.Cancelable);
        setCanceledOnTouchOutside(mDialogBuilder.CanceledOnTouchOutside);

        //全屏的背景
        ImageView v_ImageBackground = findViewById(R.id.view_DialogBackground);
        if (mDialogBuilder.Background != null) {
            mDialogBuilder.Background.processor(v_ImageBackground);
        }
    }

    //全屏Dialog
    private void initFullScreenDialog() {
        setContentView(R.layout.dialog_blur_bg);
        setFullScreen();
        ViewGroup v_RootLayout = findViewById(R.id.layout_DialogRoot);
//        ViewGroup v_RootContent = findViewById(R.id.layout_DialogContent);


        View v_DialogLayout = LayoutInflater.from(getContext()).inflate(mDialogBuilder.LayoutId, v_RootLayout, true);


        //区域外点击关闭Dialog
        ImageView v_ImageBackground = findViewById(R.id.view_DialogBackground);
        v_ImageBackground.setOnClickListener(v -> {
            if (mCanceledOnTouchOutside) {
                dismiss();
            }
        });
        v_DialogLayout.setOnClickListener(v -> {
        });

    }


    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        mCancelable = flag;
        if (!flag) {
            mCanceledOnTouchOutside = false;
        }
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        if (cancel && !mCancelable) {
            mCancelable = true;
        }
        mCanceledOnTouchOutside = cancel;
    }


    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (!mCancelable) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //全屏显示弹窗
    public void setFullScreen() {
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

}
