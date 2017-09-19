package com.rajesh.pdfdemo.ui.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

public class FrescoView extends SimpleDraweeView {
    public FrescoView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public FrescoView(Context context) {
        super(context);
    }

    public FrescoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FrescoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public FrescoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 不使用缓存
     *
     * @param uri 图片URI
     */
    public void setImageURINoCache(Uri uri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromCache(uri);
        Log.d("PIC URL:", uri.toString());
        super.setImageURI(uri);
    }

    /**
     * 使用内存缓存
     *
     * @param uri 图片URI
     */
    public void setImageURIWithMemory(Uri uri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromDiskCache(uri);
        Log.d("PIC URL:", uri.toString());
        super.setImageURI(uri);
    }
}
