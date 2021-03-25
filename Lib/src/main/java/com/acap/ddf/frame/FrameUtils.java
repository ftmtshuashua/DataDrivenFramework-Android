package com.acap.ddf.frame;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/1/18 10:13
 * </pre>
 */
class FrameUtils {

    //创建一个Fragment
    public static final Fragment instantiateFragment(@NonNull Context context,
                                                     @NonNull FragmentManager fragmentManager,
                                                     @NonNull String className,
                                                     @SuppressWarnings("unused") @Nullable Bundle args) {
        Fragment instantiate = fragmentManager.getFragmentFactory().instantiate(context.getClassLoader(), className);
        if (instantiate != null && args != null) instantiate.setArguments(args);
        return instantiate;
    }
}
