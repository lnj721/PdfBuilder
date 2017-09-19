package com.rajesh.pdfdemo.util.drag;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ViewConfiguration;

import com.rajesh.pdfdemo.MyApp;

/**
 * Created by zhufeng on 2017/3/31.
 */

public class OnContractImageDragCallback extends ItemTouchHelper.Callback {
    private boolean canDrag = false;
    private boolean canSwipe = false;
    private OnItemDragCallbackListener mAdapter;

    public OnContractImageDragCallback(OnItemDragCallbackListener mAdapter) {
        this.mAdapter = mAdapter;
    }

    public void setCanDrag(boolean canDrag) {
        this.canDrag = canDrag;
    }

    public void setCanSwipe(boolean canSwipe) {
        this.canSwipe = canSwipe;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return canSwipe;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return canDrag;
    }

    /**
     * 设置Drag/Swipe的Flag
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag = ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlag = ItemTouchHelper.START;
        return makeMovementFlags(dragFlag, swipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mAdapter.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onSwipe(viewHolder.getAdapterPosition());
    }

    /**
     * 设置触发swipe的速度
     *
     * @param defaultValue
     * @return
     */
    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        int maxVelocity = ViewConfiguration.get(MyApp.getAppContext()).getScaledMaximumFlingVelocity();
        //设置返回值为最大速度加一（不能通过用力滑动触发swipe，只能通过滑动指定距离触发swipe）
        return maxVelocity + 1;
    }

    /**
     * 设置触发Move的距离（默认是0.5F）
     *
     * @param viewHolder
     * @return
     */
    @Override
    public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
        return 0.5F;
    }

    /**
     * 设置触发swipe的距离（默认是0.5F）
     *
     * @param viewHolder
     * @return
     */
    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return 0.5F;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            //mAdapter.onStart();
            viewHolder.itemView.setAlpha(0.7F);
            viewHolder.itemView.setScaleX(1.03F);
            viewHolder.itemView.setScaleY(1.03F);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setAlpha(1.0F);
        viewHolder.itemView.setScaleX(1.0F);
        viewHolder.itemView.setScaleY(1.0F);
        mAdapter.onComplete();
    }
}