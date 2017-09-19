package com.rajesh.pdfdemo.ui.module.createpdf;

import android.content.Intent;

import com.rajesh.pdfdemo.ui.BasePresenter;
import com.rajesh.pdfdemo.ui.BaseView;
import com.rajesh.pdfdemo.ui.model.ImageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhufeng on 2017/9/18.
 */

public interface CreatePdfContract {
    interface View extends BaseView<Presenter> {

        void initWithData(List<ImageItem> imageItems);

        void jumpToAlbum(int countLimit);

        void jumpToImagePreview(int index, ArrayList<ImageItem> imageItems);

        void showDeleteConfirmDialog();

        void exitImageListEditMode();

        void setDeleteBtn(int waitToDeleteSize);

        void refreshImageList();

        void toast(String tip);

        void showPdfSavePath(String path);

        void showPdfSaveError();

    }

    interface Presenter extends BasePresenter {
        /**
         * 添加从相册选取的图片
         *
         * @param data 相册页面返回的数据
         */
        void addImages(Intent data);

        void deleteImages();

        void toggleImageStatus(int index);

        void resetWaitToDeleteImageList();

        void showImageDetailWithIndex(int index);

        void intoAlbumToAddImages();

        void createPdf(String fileName);
    }
}
