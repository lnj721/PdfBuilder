package com.rajesh.pdfdemo;

import android.app.Application;
import android.content.Context;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.rajesh.pdfdemo.constant.DeviceConstants;
import com.rajesh.pdfdemo.util.FileUtils;

/**
 * Created by zhufeng on 2017/9/18.
 */

public class MyApp extends Application {
    public static final int MAX_DISK_CACHE_VERY_LOW_SIZE = 10 * ByteConstants.MB;// 默认图极低磁盘空间缓存的最大值
    public static final int MAX_DISK_CACHE_LOW_SIZE = 30 * ByteConstants.MB;// 默认图低磁盘空间缓存的最大值
    public static final int MAX_DISK_CACHE_SIZE = 100 * ByteConstants.MB;// 默认图磁盘缓存的最大值

    private static Context mContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        //Fresco 初始化
        DiskCacheConfig diskCacheConfig = DiskCacheConfig
                .newBuilder(this)
                .setBaseDirectoryPath(FileUtils.createSDDir(FileUtils.getImagesPath()))// 缓存图片基路径
                .setBaseDirectoryName(FileUtils.DIRECTORY_IMAGES)// 文件夹名
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)// 默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_LOW_SIZE)// 缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_VERY_LOW_SIZE)// 缓存的最大大小,当设备极低磁盘空间
                .build();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();
        Fresco.initialize(this, config);

        DeviceConstants.init(mContext);
    }

    public static Context getAppContext() {
        return mContext;
    }
}
