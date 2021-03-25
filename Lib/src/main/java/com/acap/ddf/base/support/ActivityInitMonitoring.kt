package com.weather.base.base.support

import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.acap.toolkit.thread.ThreadHelper
import com.lfp.eventtree.EventChain


/**
 * Activity 初始化完成检查
 */

class ActivityInitListener(val activity: ComponentActivity) : EventChain(), LifecycleObserver {

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun call() {
        ThreadHelper.io {
            while (true) {
                Thread.sleep(100)
                if (isInterrupt) return@io
                val windowToken: IBinder? = activity?.window?.decorView?.windowToken
                if (windowToken != null) {
                    ThreadHelper.main { next() }
                    return@io
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        interrupt()
    }

}
