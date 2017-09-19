package com.rajesh.pdfdemo.ui.view.gallery;

/**
 * Created by zhufeng on 2017/1/9.
 */
public interface IZoomView {
    void reset();
    boolean isZoomToOriginalSize();
    void setSize(int width, int height);
    void setMargin(int marginLeft, int marginTop);
}
