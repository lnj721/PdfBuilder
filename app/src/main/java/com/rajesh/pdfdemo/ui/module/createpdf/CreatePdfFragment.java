package com.rajesh.pdfdemo.ui.module.createpdf;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rajesh.pdfdemo.MyApp;
import com.rajesh.pdfdemo.R;
import com.rajesh.pdfdemo.ui.adapter.ImageAdapter;
import com.rajesh.pdfdemo.ui.model.ImageItem;
import com.rajesh.pdfdemo.ui.module.ImagePreviewActivity;
import com.rajesh.pdfdemo.util.PermissionUtils;
import com.rajesh.pdfdemo.util.drag.OnContractImageDragCallback;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.FrescoEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_OK;
import static com.rajesh.pdfdemo.constant.DeviceConstants.DP;

/**
 * Created by zhufeng on 2017/9/18.
 */

public class CreatePdfFragment extends Fragment implements CreatePdfContract.View, View.OnClickListener {
    private static final int REQUEST_CODE_ALBUM = 0x0001;
    private Context mContext = null;

    private TextView createBtn;
    private EditText nameEt;
    private FloatingActionButton albumBtn;
    private LinearLayout deleteBtn;
    private TextView deleteCountTv;
    private RecyclerView contentView;
    private ImageAdapter mImageAdapter;

    private CreatePdfContract.Presenter mPresenter;

    public static CreatePdfFragment newInstance() {
        CreatePdfFragment view = new CreatePdfFragment();
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("zhufeng", "onCreateView");
        View root = inflater.inflate(R.layout.fragment_create_pdf, container, false);
        nameEt = (EditText) root.findViewById(R.id.name);
        contentView = (RecyclerView) root.findViewById(R.id.content);
        albumBtn = (FloatingActionButton) root.findViewById(R.id.album);
        deleteBtn = (LinearLayout) root.findViewById(R.id.delete_holder);
        deleteCountTv = (TextView) root.findViewById(R.id.delete_count);
        deleteBtn.setEnabled(false);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createBtn = (TextView) getActivity().findViewById(R.id.function);
        mPresenter.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_ALBUM) {
            mPresenter.addImages(data);
        }
    }

    public void initWithData(List<ImageItem> imageItems) {
        mContext = MyApp.getAppContext();
        mImageAdapter = new ImageAdapter(mContext, imageItems);
        contentView.setLayoutManager(new GridLayoutManager(mContext, 2));
        contentView.setAdapter(mImageAdapter);
        ((DefaultItemAnimator) contentView.getItemAnimator()).setSupportsChangeAnimations(false);

        OnContractImageDragCallback itemDragCallback = new OnContractImageDragCallback(mImageAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(itemDragCallback);
        helper.attachToRecyclerView(contentView);
        itemDragCallback.setCanDrag(true);
        itemDragCallback.setCanSwipe(false);
        contentView.setFocusable(false);

        deleteBtn.setOnClickListener(this);
        albumBtn.setOnClickListener(this);
        createBtn.setOnClickListener(this);

        mImageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {

            @Override
            public void onClick(int position) {
                mPresenter.showImageDetailWithIndex(position);
            }

            @Override
            public void onCheckChange(int position) {
                mPresenter.toggleImageStatus(position);
            }

            @Override
            public void onLongClick() {
                mImageAdapter.insertEditMode();
                createBtn.setText("完成");
                hideDeleteBtn();
            }

            @Override
            public void onDragComplete() {
                showDeleteBtn();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                getActivity().finish();
                break;
            case R.id.function:
                if (createBtn.getText().equals("生成")) {
                    String fileName = nameEt.getText().toString();
                    if (!TextUtils.isEmpty(fileName)) {
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                        mPresenter.createPdf(fileName);
                    } else {
                        Toast.makeText(mContext, "请输入文件名称", Toast.LENGTH_SHORT).show();
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(nameEt, InputMethodManager.SHOW_FORCED);
                    }
                } else {
                    mPresenter.resetWaitToDeleteImageList();
                }
                break;
            case R.id.delete_holder:
                showDeleteConfirmDialog();
                break;
            case R.id.album:
                if (PermissionUtils.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    mPresenter.intoAlbumToAddImages();
                } else {
                    PermissionUtils.requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void setPresenter(CreatePdfContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void jumpToAlbum(int countLimit) {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .theme(R.style.Matisse_QiYueSuo)
                .countable(true)
                .maxSelectable(countLimit)
                .imageEngine(new FrescoEngine())
                .forResult(REQUEST_CODE_ALBUM);
    }

    @Override
    public void jumpToImagePreview(int index, ArrayList<ImageItem> imageItems) {
        Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
        intent.putExtra("images", imageItems);
        intent.putExtra("index", index);
        startActivity(intent);
    }

    @Override
    public void showDeleteConfirmDialog() {
        new AlertDialog
                .Builder(getActivity(), R.style.AlertDialogTheme)
                .setTitle("提示")
                .setMessage("确定删除这些文件？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.deleteImages();
                    }
                })
                .setNegativeButton("取消", null)
                .setCancelable(true)
                .show();
    }

    private boolean hasDeleteBtnShow = false;

    private void showDeleteBtn() {
        if (!hasDeleteBtnShow) {
            hasDeleteBtnShow = true;
            contentView.setPadding(0, 0, 0, 48 * DP);
            deleteBtn.setVisibility(View.VISIBLE);
            Animation deleteAnim = AnimationUtils.loadAnimation(mContext, R.anim.up_to_show);
            deleteAnim.setFillAfter(true);
            deleteBtn.startAnimation(deleteAnim);
        }
    }

    private void hideDeleteBtn() {
        if (hasDeleteBtnShow) {
            hasDeleteBtnShow = false;
            Animation deleteAnim = AnimationUtils.loadAnimation(mContext, R.anim.down_to_hide);
            deleteAnim.setFillAfter(true);
            deleteBtn.startAnimation(deleteAnim);
            Observable
                    .timer(300, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            deleteBtn.setVisibility(View.GONE);
                            contentView.setPadding(0, 0, 0, 0);
                        }
                    });
        }
    }

    @Override
    public void exitImageListEditMode() {
        mImageAdapter.quitEditMode();
        createBtn.setText("生成");
        setDeleteBtn(0);
        hideDeleteBtn();
    }

    @Override
    public void toast(String tip) {
        Toast.makeText(mContext, tip, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setDeleteBtn(int waitToDeleteSize) {
        deleteCountTv.setText("(" + waitToDeleteSize + ")");
        deleteBtn.setEnabled(waitToDeleteSize != 0);
    }

    @Override
    public void refreshImageList() {
        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void showPdfSavePath(String path) {
        new AlertDialog
                .Builder(getActivity(), R.style.AlertDialogTheme)
                .setTitle("提示")
                .setMessage("PDF文件保存路径：\n" + path)
                .setNeutralButton("确定", null)
                .setCancelable(true)
                .show();
    }

    @Override
    public void showPdfSaveError() {
        new AlertDialog
                .Builder(getActivity(), R.style.AlertDialogTheme)
                .setTitle("提示")
                .setMessage("生成PDF文件异常，请检查存储空间")
                .setNeutralButton("确定", null)
                .setCancelable(true)
                .show();
    }
}
