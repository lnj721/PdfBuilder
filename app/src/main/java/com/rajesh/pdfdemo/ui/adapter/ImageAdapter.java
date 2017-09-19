package com.rajesh.pdfdemo.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.rajesh.pdfdemo.R;
import com.rajesh.pdfdemo.constant.DeviceConstants;
import com.rajesh.pdfdemo.ui.model.ImageItem;
import com.rajesh.pdfdemo.util.drag.OnItemDragCallbackListener;

import java.util.List;

/**
 * Created by zhufeng on 2017/7/11.
 */

public class ImageAdapter extends RecyclerView.Adapter implements OnItemDragCallbackListener {
    private Context mContext;

    private LayoutInflater mLayoutInflater;
    private List<ImageItem> mDataList;
    private OnItemClickListener onItemClickListener;

    private boolean isEditMode = false;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mInfoHeight = 0;

    public ImageAdapter(Context context, List<ImageItem> mDataList) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mDataList = mDataList;
        mWidth = DeviceConstants.SCREEN_WIDTH / 2;
        mHeight = (int) ((float) mWidth * 1.33F);
        mInfoHeight = (int) ((float) mWidth * 0.13F);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.view_image, parent, false);
        itemView.setLayoutParams(new ViewGroup.LayoutParams(mWidth, mHeight));
        return new ViewHolderMy(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ViewHolderMy vh = (ViewHolderMy) holder;
        final int finalI = position;
        final ImageItem mData = mDataList.get(position);

        //设置编辑框
        vh.check.setVisibility(isEditMode ? View.VISIBLE : View.INVISIBLE);
        vh.check.setImageResource(mData.isCheck() ? R.mipmap.icon_check_selected : R.mipmap.icon_check_normal);

        //填充图片
        Uri uri = Uri.parse("file://" + mData.getPath());
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(mWidth, mHeight))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder().setOldController(vh.img.getController()).setImageRequest(request).build();
        vh.img.setController(controller);

        //页码
        vh.info.setText(String.format("%02d", (position + 1)));

        //添加用户操作监听
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(finalI);
                }
            }
        });
        vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onLongClick();
                }
                return false;
            }
        });
        vh.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onCheckChange(finalI);
                }
            }
        });
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        /**
         * 在这里进行给原数组数据的移动
         */
        ImageItem myDataTemp = mDataList.get(fromPosition);
        mDataList.remove(fromPosition);
        mDataList.add(toPosition, myDataTemp);
        /**
         * 通知数据移动
         */
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onSwipe(int position) {
        /**
         * 原数据移除数据
         */
        mDataList.remove(position);
        /**
         * 通知移除
         */
        notifyItemRemoved(position);
    }

    @Override
    public void onComplete() {
        notifyDataSetChanged();
        if (onItemClickListener != null) {
            onItemClickListener.onDragComplete();
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolderMy extends RecyclerView.ViewHolder {
        public SimpleDraweeView img;
        public TextView info;
        public ImageView check;

        public ViewHolderMy(View itemView) {
            super(itemView);
            img = (SimpleDraweeView) itemView.findViewById(R.id.img);
            info = (TextView) itemView.findViewById(R.id.info);
            check = (ImageView) itemView.findViewById(R.id.check);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mWidth, mInfoHeight);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            info.setLayoutParams(params);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);

        void onCheckChange(int position);

        void onLongClick();

        void onDragComplete();
    }

    public void insertEditMode() {
        if (!isEditMode) {
            isEditMode = true;
            notifyDataSetChanged();
        }
    }

    public void quitEditMode() {
        if (isEditMode) {
            isEditMode = false;
            notifyDataSetChanged();
        }
    }
}