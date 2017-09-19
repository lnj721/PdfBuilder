package com.rajesh.pdfdemo.ui.view.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.rajesh.pdfdemo.ui.view.FrescoView;

/**
 * added by zhufeng on 2016/6/13.
 * modify 2016/12/26
 */
public class ZoomImageView extends FrescoView implements IZoomView {
    private static final float MAX_SCALE = 3.0f;
    private static final float ORIGINAL_SCALE = 1.0f;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private float mMidX;
    private float mMidY;
    private OnClickListener mClickListener;

    /**
     * ZoomImageView的状态
     */
    private Matrix mCurrentMatrix;
    private float mCurrentScale = 1.0f;
    private boolean mIsLeftSide = true;
    private boolean mIsRightSide = true;

    public ZoomImageView(Context context) {
        super(context);
        init();
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        mCurrentMatrix = new Matrix();

        ScaleGestureDetector.OnScaleGestureListener scaleListener = new ScaleGestureDetector
                .SimpleOnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();

                mCurrentScale *= scaleFactor;
                if (mMidX == 0f) {
                    mMidX = getWidth() / 2f;
                }
                if (mMidY == 0f) {
                    mMidY = getHeight() / 2f;
                }
                mCurrentMatrix.postScale(scaleFactor, scaleFactor, mMidX, mMidY);
                invalidate();

                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                super.onScaleEnd(detector);

                if (mCurrentScale < ORIGINAL_SCALE) {
                    reset();
                } else if (mCurrentScale > MAX_SCALE) {//超出最大后增加回弹
                    float scaleFactor = MAX_SCALE / mCurrentScale;
                    mCurrentScale = MAX_SCALE;
                    mCurrentMatrix.postScale(scaleFactor, scaleFactor, mMidX, mMidY);
                    invalidate();
                }

                checkBorder();
//                showTag();
            }
        };
        mScaleDetector = new ScaleGestureDetector(getContext(), scaleListener);

        GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mClickListener != null) {
                    mClickListener.onClick();
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                float x = e.getX();
                float y = e.getY();
                if (mCurrentScale == ORIGINAL_SCALE) {
                    float scaleFactor = MAX_SCALE / mCurrentScale;
                    mCurrentScale = MAX_SCALE;
                    if (mMidX == 0f) {
                        mMidX = getWidth() / 2f;
                    }
                    if (mMidY == 0f) {
                        mMidY = getHeight() / 2f;
                    }
                    mCurrentMatrix.postScale(scaleFactor, scaleFactor, x, y);
                    invalidate();
                } else {
                    reset();
                }
                checkBorder();
//                showTag();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                mCurrentMatrix.postTranslate(-distanceX, -distanceY);
                invalidate();
                checkBorder();
//                showTag();
                return false;
            }
        };

        mGestureDetector = new GestureDetector(getContext(), gestureListener);
    }

    @Override
    public void setImageURI(Uri uri) {
        reset();
        super.setImageURI(uri);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        reset();
        super.setImageBitmap(bm);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.concat(mCurrentMatrix);
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        if (!mScaleDetector.isInProgress()) {
            mGestureDetector.onTouchEvent(event);
        }
        return true;
    }

    @Override
    public void reset() {
        mCurrentMatrix.reset();
        mCurrentScale = ORIGINAL_SCALE;
        mIsLeftSide = true;
        mIsRightSide = true;
        invalidate();
    }

    @Override
    public boolean isZoomToOriginalSize() {
        return mCurrentScale == ORIGINAL_SCALE;
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

    public boolean isLeftSide() {
        return mIsLeftSide;
    }

    public boolean isRightSide() {
        return mIsRightSide;
    }

    public void setOnClickListener(OnClickListener listener) {
        mClickListener = listener;
    }

    /**
     * 检查图片边界是否移到view以内
     * 目的是让图片边缘不要移动到view里面
     */
    private void checkBorder() {
        RectF rectF = getDisplayRect(mCurrentMatrix);
        boolean reset = false;
        float dx = 0;
        float dy = 0;
        if (rectF.left >= 0) {
            dx = 0 - rectF.left;
            reset = true;
            mIsLeftSide = true;
        } else {
            mIsLeftSide = false;
        }
        if (rectF.top >= 0) {
            dy = getTop() - rectF.top;
            reset = true;
        }
        //考虑到viewpager拿到的getright会叠加 此处使用修正值
        if (rectF.right <= getRight() - getLeft()) {
            dx = getRight() - getLeft() - rectF.right;
            reset = true;
            mIsRightSide = true;
        } else {
            mIsRightSide = false;
        }
        if (rectF.bottom <= getHeight()) {
            dy = getHeight() - rectF.bottom;
            reset = true;
        }
        if (reset) {
            mCurrentMatrix.postTranslate(dx, dy);
            invalidate();
        }
    }

    /**
     * 获得当前缩放倍率下的图片 相对于组件左上角的位置
     *
     * @param matrix 当前的缩放倍率
     * @return RectF(l, t, r, b)
     */
    private RectF getDisplayRect(Matrix matrix) {
        //组件信息
        RectF rectF = new RectF(0, getTop(), getRight() - getLeft(), getBottom());
        //转化为缩放后的相对位置
        matrix.mapRect(rectF);
        return rectF;
    }

    private void marginView(View view, int l, int t, int r, int b) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }

    private void showTag() {
        RectF rectF = getDisplayRect(mCurrentMatrix);
        Log.i("zhufeng", "---------------------->(" + rectF.left + "," + rectF.top + "," + rectF.right + "," + rectF.bottom + ")", null);
        Log.i("zhufeng", "是否最小：" + isZoomToOriginalSize() + ", 是否靠左：" + isLeftSide() + " ,是否靠右：" + isRightSide(), null);
    }

    public interface OnClickListener {
        void onClick();
    }

}