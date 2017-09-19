package com.zhihu.matisse.engine;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by zhufeng on 2017/8/8.
 */

public class FrescoImageView extends SimpleDraweeView {

    public FrescoImageView(Context context) {
        this(context, null);
    }

    public FrescoImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrescoImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
