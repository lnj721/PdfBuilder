package com.rajesh.pdfdemo.ui.module.createpdf;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.rajesh.pdfdemo.MyApp;
import com.rajesh.pdfdemo.R;

/**
 * Created by zhufeng on 2017/9/19.
 */

public class CreatePdfActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 0x0002;

    private CreatePdfContract.Presenter mPresenter;
    private TextView functionBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pdf);

        functionBtn = (TextView) findViewById(R.id.function);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        CreatePdfFragment mView = (CreatePdfFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (mView == null) {
            mView = CreatePdfFragment.newInstance();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.contentFrame, mView, CreatePdfFragment.class.getName());
            transaction.commit();
        }
        mPresenter = new CreatePdfPresenter(new CreatePdfModel(), mView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPresenter.intoAlbumToAddImages();
                } else {
                    new AlertDialog
                            .Builder(CreatePdfActivity.this, R.style.AlertDialogTheme)
                            .setTitle("提示")
                            .setMessage("请在设置中允许读取手机存储")
                            .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri packageURI = Uri.parse("package:" + MyApp.getAppContext().getPackageName());
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("跳过", null)
                            .setCancelable(true)
                            .show();
                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (functionBtn.getText().equals("生成")) {
            finish();
        } else {
            mPresenter.resetWaitToDeleteImageList();
        }
    }
}
