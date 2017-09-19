package com.rajesh.pdfdemo.ui.module.createpdf;

import com.rajesh.pdfdemo.ui.model.ImageItem;

import java.util.ArrayList;

/**
 * Created by zhufeng on 2017/9/19.
 */

public class CreatePdfModel {
    private ArrayList<ImageItem> dataList = new ArrayList<>();
    private int waitToDeleteCount = 0;
    public static final int IMAGE_COUNT_LIMIT = 10;

    public ArrayList<ImageItem> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<ImageItem> mDataList) {
        this.dataList = mDataList;
    }

    public int getWaitToDeleteCount() {
        return waitToDeleteCount;
    }

    public void setWaitToDeleteCount(int waitToDeleteCount) {
        this.waitToDeleteCount = waitToDeleteCount;
    }
}
