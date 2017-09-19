package com.rajesh.pdfdemo.ui.view.gallery;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhufeng on 2016/6/13.
 */
public class GalleryViewPager extends ViewPager implements IZoomView {
    private ZoomImageView mCurrentView;
    private OnCustomClickListener onCustomClickListener;

    @Override
    public void reset() {
        setCurrentItem(getCurrentItem());
        invalidate();
        mCurrentView.reset();
    }

    @Override
    public boolean isZoomToOriginalSize() {
        if (mCurrentView != null) {
            return mCurrentView.isZoomToOriginalSize();
        }
        return false;
    }

    @Override
    public void setSize(int width, int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }

    @Override
    public void setMargin(int marginLeft, int marginTop) {
        marginView(this, marginLeft, marginTop, -marginLeft, -marginTop);
    }

    private void marginView(View view, int l, int t, int r, int b) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }

    public interface OnCustomClickListener {
        void onSingleTap();
    }

    public GalleryViewPager(Context context) {
        this(context, null);
    }

    public GalleryViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean hasMoved = false;
    private PointF last;
    private PointF tempPoint;

    /**
     * 事件拦截
     * 需要由子view消费的事件：
     * 1、多指操作
     * 2、单击，双击，长按
     * 3、图片处于放大状态且不在左右边界
     * 4、图片处于放大状态靠左时非右滑操作
     * 5、图片处于放大状态靠右时非左滑操作
     *
     * @param event 用于操作事件
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            //子view消费所有的多指操作事件
            return false;
        } else {
            //单指操作
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: {
                    hasMoved = false;
                    last = new PointF(event.getX(0), event.getY(0));
                    tempPoint = new PointF(event.getX(0), event.getY(0));
                    if (!mCurrentView.isZoomToOriginalSize() && !mCurrentView.isLeftSide() && !mCurrentView.isRightSide()) {
                        //单手情况下，图片放大且不在边界，子view消费本次事件
                        return false;
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    PointF curr = new PointF(event.getX(0), event.getY(0));
                    if (Math.abs(curr.x - last.x) > 5 || Math.abs(curr.y - last.y) > 5) {
                        hasMoved = true;
                    }

                    if (!mCurrentView.isZoomToOriginalSize()) {
                        boolean needToViewPager = false;
                        if (mCurrentView.isLeftSide() && curr.x - tempPoint.x > 5) {
                            //右滑
                            needToViewPager = true;
                        }
                        if (mCurrentView.isRightSide() && curr.x - tempPoint.x < -5) {
                            //左滑
                            needToViewPager = true;
                        }
                        tempPoint = new PointF(event.getX(0), event.getY(0));
                        if (!needToViewPager) {
                            //图片处于放大状态,靠左时非右滑操作或者靠右时非左滑操作
                            return false;
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if (!hasMoved) {
                        //在没有移动的情况下，子view消费抬起事件（子view的单击和双击事件）
                        return false;
                    }
                    break;
                }
                default:
                    break;
            }
        }
        try {
            //给viewpager进行滑动处理
            return super.onInterceptTouchEvent(event);
        } catch (IllegalArgumentException e) {
            Log.i("zhufeng", "viewpager滑动处理失败");
            return true;
        }
    }

    public void setOnCustomClickListener(OnCustomClickListener onCustomClickListener) {
        this.onCustomClickListener = onCustomClickListener;
    }

    public void setZoomView(ZoomImageView currentView) {
        this.mCurrentView = null;
        this.mCurrentView = currentView;
        mCurrentView.setOnClickListener(new ZoomImageView.OnClickListener() {
            @Override
            public void onClick() {
                if (onCustomClickListener != null) {
                    onCustomClickListener.onSingleTap();
                }
            }
        });
    }
}