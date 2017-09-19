package com.rajesh.pdfdemo.ui.module;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.rajesh.pdfdemo.util.BitmapUtils;
import com.rajesh.pdfdemo.ui.model.ImageItem;
import com.rajesh.pdfdemo.MyApp;
import com.rajesh.pdfdemo.R;
import com.rajesh.pdfdemo.ui.view.gallery.GalleryViewPager;
import com.rajesh.pdfdemo.ui.view.gallery.ZoomImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.rajesh.pdfdemo.constant.DeviceConstants.DP;
import static com.rajesh.pdfdemo.constant.DeviceConstants.SCREEN_HEIGHT;
import static com.rajesh.pdfdemo.constant.DeviceConstants.SCREEN_WIDTH;
import static com.rajesh.pdfdemo.constant.DeviceConstants.STATUS_BAR_HEIGHT;

/**
 * Created by zhufeng on 2017/8/14.
 */
public class ImagePreviewActivity extends AppCompatActivity {
    private LinearLayout actionBar;
    private ImageButton backBtn;
    private GalleryViewPager contentView;
    private TextView countTv;

    private Context mContext = null;
    private HashMap<Integer, ZoomImageView> viewMap = new HashMap<>();
    private ArrayList<ImageItem> mDataList = new ArrayList<>();
    private ImagePagerAdapter mAdapter;
    private int count = 0;
    private int currentItem = 0;
    private boolean isOperateShow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initView();
        initData();
        initEvents();
    }

    private void initView() {
        actionBar = (LinearLayout) findViewById(R.id.action);
        backBtn = (ImageButton) findViewById(R.id.back);
        contentView = (GalleryViewPager) findViewById(R.id.content);
        countTv = (TextView) findViewById(R.id.count);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            actionBar.setPadding(0, STATUS_BAR_HEIGHT, 0, 0);
        } else {
            actionBar.setPadding(0, 0, 0, 0);
        }
    }

    private void initData() {
        mContext = MyApp.getAppContext();
        mDataList = (ArrayList<ImageItem>) getIntent().getSerializableExtra("images");
        currentItem = getIntent().getIntExtra("index", 0);
        if (mDataList == null || mDataList.size() == 0) {
            finish();
            return;
        }
        count = mDataList.size();

        mAdapter = new ImagePagerAdapter();
        contentView.setPageMargin(10 * DP);
        contentView.setAdapter(mAdapter);
        contentView.setOffscreenPageLimit(1);
        contentView.setCurrentItem(currentItem, false);

        countTv.setText(String.format("%1$d / %2$d", (currentItem + 1), count));
    }

    private void initEvents() {
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        contentView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewMap.get(currentItem).reset();
                currentItem = position;
                countTv.setText(String.format("%1$d / %2$d", (currentItem + 1), count));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        contentView.setOnCustomClickListener(new GalleryViewPager.OnCustomClickListener() {
            @Override
            public void onSingleTap() {
                if (isOperateShow) {
                    hideOperateHolder();
                } else {
                    showOperateHolder();
                }
            }
        });
    }

    /**
     * viewpager适配器
     */
    private class ImagePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            ZoomImageView zoomImage = viewMap.get(position);
            ((GalleryViewPager) container).setZoomView(zoomImage);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ZoomImageView zoomImage = viewMap.get(position);
            if (zoomImage == null) {
                zoomImage = setImageToIndex(position);
            }
            container.addView(zoomImage);
            return zoomImage;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.i("zhufeng", "remove: " + position, null);
            container.removeView((View) object);
            ZoomImageView zoomImage = viewMap.get(position);
            if (zoomImage != null) {
                zoomImage.setImageBitmap(null);
                viewMap.remove(position);
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private ZoomImageView setImageToIndex(int index) {
        ZoomImageView zoomImage = new ZoomImageView(mContext);
        String path = mDataList.get(index).getPath();
        Point size = BitmapUtils.compressWidthAndHeight(path, SCREEN_WIDTH, SCREEN_HEIGHT);
        Uri uri = Uri.parse("file://" + path);
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(size.x, size.y))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder().setOldController(zoomImage.getController()).setImageRequest(request).build();
        zoomImage.setController(controller);
        viewMap.put(index, zoomImage);
        return zoomImage;
    }

    private void showOperateHolder() {
        isOperateShow = true;
        actionBar.setVisibility(View.VISIBLE);
        Animation startAnim = AnimationUtils.loadAnimation(this, R.anim.action_bar_show);
        startAnim.setFillAfter(true);
        actionBar.startAnimation(startAnim);
    }

    private void hideOperateHolder() {
        isOperateShow = false;
        Animation hideAnim = AnimationUtils.loadAnimation(this, R.anim.action_bar_hide);
        hideAnim.setFillAfter(true);
        actionBar.startAnimation(hideAnim);
        Observable
                .timer(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        actionBar.setVisibility(View.GONE);
                        actionBar.clearAnimation();
                    }
                });
    }
}