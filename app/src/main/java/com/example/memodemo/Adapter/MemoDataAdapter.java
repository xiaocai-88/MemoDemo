package com.example.memodemo.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memodemo.R;
import com.example.memodemo.domain.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * description recyclerView适配器,显示item
 * create by xiaocai on 2020/6/12
 */
public class MemoDataAdapter extends RecyclerView.Adapter<MemoDataAdapter.InnerHolder> {

    private static final String TAG = "MemoDataAdapter";
    private List<Record> mData = new ArrayList<>();
    private OnItemClickLinstener mLinstener = null;
    private OnItemLongClickListener mLongClickListner = null;

    /**
     * 设置布局
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new InnerHolder(view);
    }

    /**
     * 设置数据
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        //获取数据
        int itemId = mData.get(position).getId();
        final String itemTitle = mData.get(position).getTitleName();
        final String itemBody = mData.get(position).getTextBody();
        final String itemCreateTime = mData.get(position).getCreateTime();
        final String itemModifyTime = mData.get(position).getModifyTime();
        final boolean tipsChecked = mData.get(position).getTipsChecked();
//        mData.get(position).get
        Log.d(TAG, "itemId is " + itemId);
        Log.d(TAG, "itemName is " + itemTitle);
        Log.d(TAG, "itemBody is " + itemBody);
        Log.d(TAG, "itemModifyTime is " + itemModifyTime);
        Log.d(TAG, "tipsChecked is " + tipsChecked);
        //设置数据
        holder.mItemId.setText(itemId + "");
        holder.mItemTitle.setText(itemTitle);
        holder.mItemBody.setText(itemBody);
        if (itemModifyTime != null) {
            holder.mModifyTime.setText("amend：" + itemModifyTime);
        } else {
            holder.mModifyTime.setText("");
        }
        holder.mCreateTime.setText("create： " + itemCreateTime);

        //设置item的点击事件（点击跳转到编辑界面）
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinstener.onClick(position, itemTitle, itemBody, tipsChecked);
            }
        });

        //设置item的长按事件（长按删除）
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mLongClickListner.onLongClick(position, itemTitle, itemBody);
                return true;
            }
        });
    }

    /**
     * item的长按事件
     *
     * @param listener
     */
    public void setOnItemLongClickLinstener(OnItemLongClickListener listener) {
        this.mLongClickListner = listener;
    }

    /**
     * 从数据集合中删除该记录
     *
     * @param position
     */
    public void removeItem(int position) {
        this.mData.remove(position);
    }


    /**
     * item长按事件的接口
     */
    public interface OnItemLongClickListener {
        void onLongClick(int position, String title, String body);
    }

    /**
     * item的点击事件
     *
     * @param linstener
     */
    public void setOnItemClickLinstener(OnItemClickLinstener linstener) {
        this.mLinstener = linstener;
    }

    /**
     * item点击事件的接口
     */
    public interface OnItemClickLinstener {
        void onClick(int position, String title, String body, boolean tipsChecked);
    }

    /**
     * 数据库中有几条数据就显示多少条
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 将外部数据库中遍历的数据传到adapter中
     * 将数据设置到itemview中
     *
     * @param recordList
     */
    public void setData(List<Record> recordList) {
        mData.clear();
        this.mData = recordList;
        notifyDataSetChanged();
    }

    /**
     * 找到控件
     */
    public class InnerHolder extends RecyclerView.ViewHolder {

        private final TextView mItemId;
        private final TextView mItemTitle;
        private final TextView mItemBody;
        private final TextView mCreateTime;
        private final TextView mModifyTime;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mItemId = itemView.findViewById(R.id.item_memo_id);
            mItemTitle = itemView.findViewById(R.id.item_memo_title);
            mItemBody = itemView.findViewById(R.id.item_memo_body);
            mCreateTime = itemView.findViewById(R.id.memo_create_time_tv);
            mModifyTime = itemView.findViewById(R.id.memo_modify_time_tv);
        }
    }
}
