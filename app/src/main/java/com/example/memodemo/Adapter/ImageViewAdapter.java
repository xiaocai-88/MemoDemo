package com.example.memodemo.Adapter;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memodemo.R;
import com.example.memodemo.domain.ImageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * description 显示照片的适配器
 * create by xiaocai on 2020/6/29
 */
public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.InnerHolder> {

    private static final String TAG = "ImageViewAdapter";
    private List<ImageItem> mImageItems = new ArrayList<>();
    private List<ImageItem> mSelectedItem = new ArrayList<>();
    public static final int MAX_SELECTED_COUNT = 2;
    public static final int COLUMN_COUNT = 3;
    //暴露方法出去给外部设置最多可选张数
    private int maxSelectedCount = MAX_SELECTED_COUNT;
    private OnItemSelectedChangeListener mOnItemSelectedChangeListener = null;

    public int getMaxSelectedCount() {
        return maxSelectedCount;
    }

    public List<ImageItem> getSelectedItem() {
        return mSelectedItem;
    }

    public void setSelectedItem(List<ImageItem> selectedItem) {
        mSelectedItem = selectedItem;
    }

    public void setMaxSelectedCount(int maxSelectedCount) {
        this.maxSelectedCount = maxSelectedCount;
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_view, parent, false);
        //将图片与屏幕适配
        Point point = new Point();
        ((WindowManager) parent.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(point.x / COLUMN_COUNT, point.x / COLUMN_COUNT);
        view.setLayoutParams(layoutParams);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InnerHolder holder, int position) {
        View itemView = holder.itemView;
        ImageView image = itemView.findViewById(R.id.image_iv);
        final View cover = itemView.findViewById(R.id.image_cover);
        final CheckBox checkBox = itemView.findViewById(R.id.image_check_box);
        String imagePath = mImageItems.get(position).getPath();
        Log.d(TAG, "image path is " + imagePath);
        Glide.with(image.getContext()).load(imagePath).into(image);
        //设置图片选中状态
        final ImageItem imageItme = mImageItems.get(position);
        if (mSelectedItem.contains(imageItme)) {
            //如果已经添加进去了，设置CheckBox不可选，设置阴影，设置勾选样式
            checkBox.setChecked(false);
            checkBox.setButtonDrawable(holder.itemView.getContext().getDrawable(R.mipmap.checkitem));
            cover.setVisibility(View.VISIBLE);
        } else {
            //如果未添加进去了，设置CheckBox可选，取消阴影，取消勾选样式
            checkBox.setChecked(true);
            checkBox.setButtonDrawable(holder.itemView.getContext().getDrawable(R.mipmap.itemtwo));
            cover.setVisibility(View.GONE);
        }

        //设置图片的点击事件
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (mSelectedItem.contains(imageItme)) {
                    //如果点击后已经存在在集合中了
                    //就改变其状态，设为未选择的状态
                    mSelectedItem.remove(imageItme);
                    checkBox.setChecked(true);
                    checkBox.setButtonDrawable(holder.itemView.getContext().getDrawable(R.mipmap.itemtwo));
                    cover.setVisibility(View.GONE);
                } else {
                    //对可选最大值进行一个判断
                    if (mSelectedItem.size() > maxSelectedCount - 1) {
                        Toast.makeText(checkBox.getContext(), "最多可选" + maxSelectedCount + "张图片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //设为可选状态
                    mSelectedItem.add(imageItme);
                    checkBox.setChecked(false);
                    checkBox.setButtonDrawable(holder.itemView.getContext().getDrawable(R.mipmap.checkitem));
                    cover.setVisibility(View.VISIBLE);
                }
                if (mOnItemSelectedChangeListener != null) {
                    mOnItemSelectedChangeListener.onItemSelectedChange(mSelectedItem);
                }
            }
        });
    }

    /**
     * 将adapter中选中的数据设置进去
     *
     * @param listener
     */
    public void setOnItemSelectedChangeListener(OnItemSelectedChangeListener listener) {
        this.mOnItemSelectedChangeListener = listener;
    }

    /**
     * 暴露接口让外部知道哪些image被选中
     */
    public interface OnItemSelectedChangeListener {
        void onItemSelectedChange(List<ImageItem> SelectedItem);
    }


    @Override
    public int getItemCount() {
        return mImageItems.size();
    }

    /**
     * 将adapter中的数据重置，销毁当前已选的image
     */
    public void reSetDate() {
        mSelectedItem.clear();
    }

    /**
     * 设置适配器的数据
     *
     * @param itemlist
     */
    public void setDate(List<ImageItem> itemlist) {
        mImageItems.clear();
        mImageItems.addAll(itemlist);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
