package com.rajesh.pdfdemo.util;

import android.os.Environment;
import android.util.Log;

import com.rajesh.pdfdemo.MyApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class FileUtils {
    public static final String DIRECTORY_IMAGES = "images";
    private static final String DIRECTORY_ROOT = MyApp.getAppContext().getDir("qys", MODE_PRIVATE).getAbsolutePath();

    private static final String QYS_EXTERNAL = "QiYueSuo";
    private static final String QYS_EXTERNAL_IMAGE = "images";
    private static final String QYS_EXTERNAL_IMAGE_HIDE = ".images";
    private static final String QYS_EXTERNAL_IMAGE_BIG = "big";
    private static final String QYS_EXTERNAL_IMAGE_SMALL = "small";

    public FileUtils() {

    }

    /**
     * /data/data/app_qys
     *
     * @return
     */
    public static String getInternalQiYueSuoPath() {
        String fileRoot = DIRECTORY_ROOT;
        if (!isFileExists(fileRoot)) {
            createSDDir(fileRoot);
        }
        showPathTag(fileRoot);
        return fileRoot;
    }

    /**
     * sdcard
     *
     * @return
     */
    public static String getSDPath() {
        return Environment.getExternalStorageDirectory().getPath() + File.separator;
    }

    /**
     * sdcard/QiYueSuo
     *
     * @return
     */
    public static String getExternalQiYueSuoPath() {
        String externalPath = getSDPath() + QYS_EXTERNAL;
        if (!isFileExists(externalPath))
            createSDDir(externalPath);
        showPathTag(externalPath);
        return externalPath;
    }

    /**
     * sdcard/QiYueSuo/image
     *
     * @return
     */
    public static String getExternalQiYueSuoImagePath() {
        String externalPath = getExternalQiYueSuoPath() + File.separator + QYS_EXTERNAL_IMAGE;
        if (!isFileExists(externalPath))
            createSDDir(externalPath);
        showPathTag(externalPath);
        return externalPath;
    }

    /**
     * sdcard/QiYueSuo/.image
     *
     * @return
     */
    public static String getHideExternalQiYueSuoImagePath() {
        String externalPath = getExternalQiYueSuoPath() + File.separator + QYS_EXTERNAL_IMAGE_HIDE;
        if (!isFileExists(externalPath))
            createSDDir(externalPath);
        showPathTag(externalPath);
        return externalPath;
    }

    /**
     * sdcard/QiYueSuo/.image/big
     *
     * @return
     */
    public static String getHideExternalQiYueSuoBigImagePath() {
        String externalPath = getHideExternalQiYueSuoImagePath() + File.separator + QYS_EXTERNAL_IMAGE_BIG;
        if (!isFileExists(externalPath))
            createSDDir(externalPath);
        showPathTag(externalPath);
        return externalPath;
    }

    /**
     * sdcard/QiYueSuo/.image/small
     * 存放缩略图（Fresco不需要这一项）
     *
     * @return
     */
    public static String getHideExternalQiYueSuoSmallImagePath() {
        String externalPath = getHideExternalQiYueSuoImagePath() + File.separator + QYS_EXTERNAL_IMAGE_SMALL;
        if (!isFileExists(externalPath))
            createSDDir(externalPath);
        showPathTag(externalPath);
        return externalPath;
    }

    /**
     * 创建文件
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static File createSDFile(String path) throws IOException {
        File file = new File(path);
        file.createNewFile();
        return file;
    }

    /**
     * 删除文件
     *
     * @param path
     * @return
     */
    public static boolean deleteSDFile(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 创建文件夹
     *
     * @param path
     * @return
     */
    public static File createSDDir(String path) {
        File dir = new File(path);
        boolean result = dir.mkdirs();
        return dir;
    }

    /**
     * 删除文件夹及其目录下的子文件
     *
     * @param path
     * @return
     */
    public static boolean deleteSDDir(String path) {
        File rootFile = new File(path);
        if (rootFile.exists() && rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                Log.i("zhufeng", "删除文件：" + file.getAbsolutePath() + " name:" + file.getName());
                if (file.isDirectory()) {
                    deleteSDDir(file.getPath());
                    file.delete();
                } else {
                    file.delete();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 判断SD卡上的文件夹(文件)是否存在
     *
     * @param path
     * @return
     */
    public static boolean isFileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static String getImagesPath() {
        String directorPath = getInternalQiYueSuoPath() + File.separator + DIRECTORY_IMAGES;
        if (!isFileExists(directorPath))
            createSDDir(directorPath);
        showPathTag(directorPath);
        return directorPath;
    }

    public static void writeToFile(String fileName, byte[] bytes) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            fos.write(bytes);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Couldn't write to " + fileName, e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void showPathTag(String path) {
        Log.i("zhufeng", path, null);
    }
}
