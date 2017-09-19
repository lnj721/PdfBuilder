package com.rajesh.pdfdemo.util.drag;

/**
 * Created by zhufeng on 2017/3/31.
 */

public interface OnItemDragCallbackListener {
    /**
     * @param fromPosition 起始位置
     * @param toPosition   移动的位置
     */
    void onMove(int fromPosition, int toPosition);

    void onSwipe(int position);

    //void onStart();

    void onComplete();
}
