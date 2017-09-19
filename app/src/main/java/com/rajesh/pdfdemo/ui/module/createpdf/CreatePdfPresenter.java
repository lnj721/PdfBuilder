package com.rajesh.pdfdemo.ui.module.createpdf;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.rajesh.pdfdemo.ui.model.ImageItem;
import com.rajesh.pdfdemo.util.FileUtils;
import com.zhihu.matisse.Matisse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by zhufeng on 2017/9/18.
 */

public class CreatePdfPresenter implements CreatePdfContract.Presenter {
    private CreatePdfModel mData;
    private CreatePdfContract.View mView;

    public CreatePdfPresenter(@NonNull CreatePdfModel createPdfModel, @NonNull CreatePdfContract.View createPdfView) {
        createPdfView.setPresenter(this);
        mData = createPdfModel;
        mView = createPdfView;
    }

    @Override
    public void start() {
        mView.initWithData(mData.getDataList());
    }

    @Override
    public void addImages(Intent data) {
        List<String> pathList = Matisse.obtainPathResult(data);
        for (int i = 0; i < pathList.size(); i++) {
            ImageItem item = new ImageItem();
            item.setPath(pathList.get(i));
            item.setCheck(false);
            mData.getDataList().add(item);
        }
        mView.refreshImageList();
    }

    @Override
    public void deleteImages() {
        int length = mData.getDataList().size();
        for (int i = length - 1; i >= 0; i--) {
            if (mData.getDataList().get(i).isCheck()) {
                mData.getDataList().remove(i);
            }
        }
        mView.refreshImageList();
        mView.exitImageListEditMode();
    }

    @Override
    public void toggleImageStatus(int index) {
        boolean isCheck = !mData.getDataList().get(index).isCheck();
        mData.getDataList().get(index).setCheck(isCheck);
        mView.refreshImageList();
        int count = mData.getWaitToDeleteCount();
        if (isCheck) {
            mData.setWaitToDeleteCount(++count);
        } else {
            mData.setWaitToDeleteCount(--count);
        }
        mView.setDeleteBtn(mData.getWaitToDeleteCount());
    }

    @Override
    public void resetWaitToDeleteImageList() {
        int length = mData.getDataList().size();
        for (int i = length - 1; i >= 0; i--) {
            mData.getDataList().get(i).setCheck(false);
        }
        mView.refreshImageList();
        mView.exitImageListEditMode();
    }

    @Override
    public void showImageDetailWithIndex(int index) {
        mView.jumpToImagePreview(index, mData.getDataList());
    }

    @Override
    public void intoAlbumToAddImages() {
        if (mData.getDataList().size() < CreatePdfModel.IMAGE_COUNT_LIMIT) {
            mView.jumpToAlbum(CreatePdfModel.IMAGE_COUNT_LIMIT - mData.getDataList().size());
        }else{
            mView.toast("已达图片数量上限");
        }
    }

    @Override
    public void createPdf(String fileName) {
        if(mData.getDataList().size() == 0){
            mView.toast("请先选择要生成PDF的图片");
            return;
        }
        String path = FileUtils.getExternalQiYueSuoImagePath() + File.separator + fileName + ".pdf";

        try {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            Background event = new Background();
            writer.setPageEvent(event);
            document.open();
            for (int i = 0; i < mData.getDataList().size(); i++) {
                document.newPage();
                Image img = Image.getInstance(mData.getDataList().get(i).getPath());
                img.scaleToFit(PageSize.A4.getWidth() - 30, PageSize.A4.getHeight() - 30);
                Log.i("zhufeng", "图片尺寸：(" + img.getScaledWidth() + "," + img.getScaledHeight() + ")");
                Log.i("zhufeng", "页面尺寸：(" + PageSize.A4.getWidth() + "," + PageSize.A4.getHeight() + ")");
                img.setAbsolutePosition((PageSize.A4.getWidth() - img.getScaledWidth()) / 2, (PageSize.A4.getHeight() - img.getScaledHeight()) / 2);
                document.add(img);
                Log.i("zhufeng", "已完成：" + i);
            }
            document.close();
            mView.showPdfSavePath(path);
        } catch (IOException e) {
            e.printStackTrace();
            mView.showPdfSaveError();
        } catch (DocumentException e) {
            e.printStackTrace();
            mView.showPdfSaveError();
        }
    }

    public class Background extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            //color
            PdfContentByte canvas = writer.getDirectContentUnder();
            Rectangle rect = document.getPageSize();
            canvas.setColorFill(BaseColor.WHITE);
            canvas.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
            canvas.fill();

            //border
            PdfContentByte canvasBorder = writer.getDirectContent();
            Rectangle rectBorder = document.getPageSize();
            rectBorder.setBorder(Rectangle.BOX);
            rectBorder.setBorderWidth(15);
            rectBorder.setBorderColor(BaseColor.WHITE);
            rectBorder.setUseVariableBorders(true);
            canvasBorder.rectangle(rectBorder);
        }
    }
}
