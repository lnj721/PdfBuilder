package com.rajesh.pdfdemo.ui.model;

import java.io.Serializable;

/**
 * Created by zhufeng on 2017/7/11.
 */

public class ImageItem implements Serializable, Cloneable {
    private String path;
    private boolean check = false;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public String toString() {
        return "ImageItem{" + '\n' +
                "    path         ='" + path + '\n' +
                "    check        =" + check + '\n' +
                '}';
    }

    public ImageItem clone() {
        ImageItem o = null;
        try {
            o = (ImageItem) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
