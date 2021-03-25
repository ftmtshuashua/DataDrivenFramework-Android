package com.acap.ddf.frame;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.weather.base.R;
import com.weather.base.base.BaseActivity;

/**
 * <pre>
 * Tip:
 *      框架中用于显示Fragment的Activity
 *
 * Created by ACap on 2021/1/7 18:35
 * </pre>
 */
public class FrameActivity extends BaseActivity {

    public static final int Fragment_Content_ID = R.id.fragment_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
        setFrameContainer(new FrameEntryContainer(this.getClass(), Fragment_Content_ID));
        setFrameInit(savedInstanceState);
    }
}
