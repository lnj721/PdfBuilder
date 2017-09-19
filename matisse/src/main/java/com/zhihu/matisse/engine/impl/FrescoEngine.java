/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.engine.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.zhihu.matisse.engine.ImageEngine;
import com.zhihu.matisse.engine.FrescoImageView;

/**
 * {@link ImageEngine} implementation using Glide.
 */

public class FrescoEngine implements ImageEngine {

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, FrescoImageView imageView, Uri uri) {
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(resize, resize))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder().setOldController(imageView.getController()).setImageRequest(request).build();
        imageView.setController(controller);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, FrescoImageView imageView, Uri uri) {
        loadThumbnail(context, resize, placeholder, imageView, uri);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, FrescoImageView imageView, Uri uri) {
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(resizeX, resizeY))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder().setOldController(imageView.getController()).setImageRequest(request).build();
        imageView.setController(controller);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, FrescoImageView imageView, Uri uri) {
        loadImage(context, resizeX, resizeY, imageView, uri);
    }

    @Override
    public boolean supportAnimatedGif() {
        return false;
    }

}
