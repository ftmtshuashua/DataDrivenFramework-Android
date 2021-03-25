package com.acap.ddf.frame;

import java.util.List;

/**
 * <pre>
 * Tip:
 *      Frame栈信息变化
 *
 * Created by ACap on 2021/1/18 14:20
 * </pre>
 */
public interface OnFrameStackChangeListener {
    /**
     * 当栈初始化
     */
    void onInit(List<FrameEntry> entrys);

    /**
     * 入栈
     */
    void onPush(FrameEntry entry);

    /**
     * 出栈
     */
    void onPop(FrameEntry entry);
}
