package com.rajesh.pdfdemo.util;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.rajesh.pdfdemo.MyApp;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zhufeng on 2016/6/7.
 */
public class BitmapUtils {

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight, Bitmap.Config config) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = config;
        return BitmapFactory.decodeFile(filePath, options);
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap compressBitmapWithSize(String filePath, int reqWidth, int reqHeight, Bitmap.Config config) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = config;
        options.outWidth = reqWidth;
        options.outHeight = reqHeight;
        return BitmapFactory.decodeFile(filePath, options);
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Point compressWidthAndHeight(String filePath, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int w = options.outWidth;
        int h = options.outHeight;
        float widthScale = (float) maxWidth / (float) w;
        float heightScale = (float) maxHeight / (float) h;

        float targetScale;
        if (widthScale >= 1 && heightScale >= 1) {
            targetScale = 1.0F;
        } else {
            targetScale = widthScale < heightScale ? widthScale : heightScale;
        }
        return new Point((int) (w * targetScale), (int) (h * targetScale));
    }

    public static Point compressWidthAndHeight(Bitmap bitmap, int maxWidth, int maxHeight) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float widthScale = (float) maxWidth / (float) w;
        float heightScale = (float) maxHeight / (float) h;

        float targetScale;
        if (widthScale >= 1 && heightScale >= 1) {
            targetScale = 1.0F;
        } else if (widthScale > heightScale) {
            targetScale = heightScale;
        } else {
            targetScale = widthScale;
        }
        return new Point((int) (w * targetScale), (int) (h * targetScale));
    }

    /**
     * 压缩保存
     *
     * @param bitmap
     * @param path
     * @param maxWidth
     * @param maxHeight
     * @param maxSize   单位为K
     */
    public static boolean compressToSave(Bitmap bitmap, String path, int maxWidth, int maxHeight, int maxSize) {
        Point targetWidthAndHeight = compressWidthAndHeight(bitmap, maxWidth, maxHeight);
        Bitmap targetBitmap = zoomImage(bitmap, targetWidthAndHeight.x, targetWidthAndHeight.y);
        if (targetBitmap == bitmap) {
            Log.e("zhufeng", "压缩失败！没有内存创建新的bitmap了", null);
            return false;
        }
        recycleBitmap(bitmap);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        targetBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while ((baos.toByteArray().length / 1024) >= maxSize) {
            baos.reset();
            options -= 5;
            targetBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        recycleBitmap(targetBitmap);
        saveByteArray(baos.toByteArray(), path);
        return true;
    }

    public static void getSmallBitmap(String filePath, long targetSize) {
        long currSize = getImageSize(filePath);
        while (currSize > targetSize) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            saveBitmap(BitmapFactory.decodeFile(filePath, options), filePath);
            currSize = getImageSize(filePath);
        }
    }

    /***
     * 图片的缩放方法
     *
     * @param origin    ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap origin, double newWidth, double newHeight) {
        Bitmap returnBitmap = null;
        float width = origin.getWidth();
        float height = origin.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        try {
            returnBitmap = Bitmap.createBitmap(origin, 0, 0, (int) width, (int) height, matrix, true);
        } catch (OutOfMemoryError e) {
            return origin;
        }
        return returnBitmap;
    }

    /**
     * 裁剪原图中间的正方形区域
     * 用于滤镜页面效果预览
     *
     * @param bitmap
     * @param isRecycled
     * @return
     */
    public static Bitmap cropRectangle(Bitmap bitmap, boolean isRecycled) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int wh = w > h ? h : w;

        int retX = w > h ? (w - h) / 2 : 0;
        int retY = w > h ? 0 : (h - w) / 2;

        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
        if (isRecycled) {
            recycleBitmap(bitmap);
        }
        return bmp;
    }

    /**
     * 对于不需要透明度的图片来说，RGB_565降低一倍的字节
     *
     * @param path
     * @return
     */
    public static Bitmap compressWith565(String path) {
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bm = BitmapFactory.decodeFile(path, options2);
        Log.i("zhufeng", "压缩后图片的大小" + (bm.getByteCount() / 1024 / 1024) + "M, 宽度:" + bm.getWidth() + "高度:" + bm.getHeight());
        return bm;
    }

    public static boolean saveBitmap(Bitmap bitmap, String path) {
        return saveBitmap(bitmap, path, Bitmap.CompressFormat.JPEG, 90, true, false);
    }

    public static boolean saveBitmap(Bitmap bitmap, String path, int quality) {
        return saveBitmap(bitmap, path, Bitmap.CompressFormat.JPEG, quality, true, false);
    }

    public static boolean saveBitmap(Bitmap bitmap, String path, Bitmap.CompressFormat format) {
        return saveBitmap(bitmap, path, format, 90, true, false);
    }

    //保存bitmap到文件
    public static boolean saveBitmap(Bitmap bitmap, String path, Bitmap.CompressFormat format, int quality, boolean needRecycler, boolean needShowInGalleryImmediately) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(format, quality, out)) {
                out.flush();
                out.close();
            }
            //通知图库更新
            if (needShowInGalleryImmediately) {
                MyApp.getAppContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            }
            //回收Bitmap
            if (needRecycler) {
                recycleBitmap(bitmap);
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 保存字节数组到文件
     *
     * @param bytes
     * @param path
     * @return
     */
    public static boolean saveByteArray(byte[] bytes, String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            bStream = new BufferedOutputStream(fileOutputStream);
            bStream.write(bytes);
            bStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static byte[] bitmap2Bytes(Bitmap bm, boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            recycleBitmap(bm);
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 打印图片信息
     *
     * @param filePath 图片存储路径
     */
    public static long getImageSize(String filePath) {
        File imageFile = new File(filePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        Log.i("zhufeng", "--------------------->图片大小：" + (imageFile.length() / 1024) + "K" + " (宽：" + options.outWidth + " 高：" + options.outHeight + ")");
        return imageFile.length();
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    public static String getRealFilePath(final Uri uri) {
        if (uri == null) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = MyApp.getAppContext().getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
