package com.rajesh.pdfdemo.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import com.rajesh.pdfdemo.MyApp;
import com.rajesh.pdfdemo.ui.module.createpdf.CreatePdfActivity;

/**
 * Created by zhufeng on 2017/9/19.
 */

public class PermissionUtils {

    public static boolean checkPermission(String permission) {
        boolean result = true;
        int targetSdkVersion = 24;
        try {
            final PackageInfo info = MyApp.getAppContext().getPackageManager().getPackageInfo(MyApp.getAppContext().getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                result = MyApp.getAppContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            } else {
                result = PermissionChecker.checkSelfPermission(MyApp.getAppContext(), permission) == PermissionChecker.PERMISSION_GRANTED;
            }
        }
        return result;
    }

    public static void requestPermission(Activity activity, String permission){
        ActivityCompat.requestPermissions(
                activity,
                new String[]{permission},
                CreatePdfActivity.REQUEST_CODE_READ_EXTERNAL_STORAGE
        );
    }
}
