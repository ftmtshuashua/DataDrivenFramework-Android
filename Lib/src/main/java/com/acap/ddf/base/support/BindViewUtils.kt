package com.weather.base.base.support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * <pre>
 * Tip:
 *      自动导入布局资源文件
 *
 * Function:
 *
 * Created by LiFuPing on 2018/12/5 09:00
 * </pre>
 */


/** 用于绑定View的布局 */
@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class BindView(val value: Int)

/** 用于绑定DataBinding的布局 */
@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class DataBindingLayout(@LayoutRes val value: Int)

/**
 * View 绑定工具
 */
object BindViewUtils {

    fun <T : Any> getBindView(target: T, inflater: LayoutInflater, container: ViewGroup?): View? {
        return getBindView(target, inflater, container, false)
    }

    /**
     * 获得View
     */
    fun <T : Any> getBindView(
        target: T,
        inflater: LayoutInflater,
        container: ViewGroup?,
        attach: Boolean
    ): View? {
        val annotation = target::class.java.getAnnotation(BindView::class.java)
        return if (annotation == null) null else inflater.inflate(
            annotation.value,
            container,
            attach
        )
    }


    /**
     * 获得BindView的布局ID
     */
    fun <T : Any> geBindView(target: T): Int {
        return target::class.java.getAnnotation(BindView::class.java)?.value ?: -1
    }


    /**
     * 获得DataBinging的布局ID
     */
    fun <T : Any> getDataBindingLayout(target: T): Int {
        return target::class.java.getAnnotation(DataBindingLayout::class.java)?.value ?: -1
    }

}


