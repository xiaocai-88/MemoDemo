package com.example.memodemo.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memodemo.R;
import com.example.memodemo.data.Record;

import java.util.List;

/**
 * description recyclerView适配器,显示item
 * create by xiaocai on 2020/6/12
 */
public class MemoDataAdapter extends RecyclerView.Adapter<MemoDataAdapter.InnerHolder> {

    private List<Record> mData = null;
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
//        Log.d(TAG,"itemId is "+itemId);
//        Log.d(TAG,"itemName is "+itemName);
//        Log.d(TAG,"itemBody is "+itemBody);
        //设置数据
        holder.mItemId.setText(itemId + "");
        holder.mItemTitle.setText(itemTitle);
        holder.mItemBody.setText(itemBody);

        //设置item的点击事件（点击跳转到编辑界面）
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinstener.onClick(position, itemTitle, itemBody);
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
     * 创建弹窗让用户判断是否要删除这些内容
     *
     * @param itemTitle
     * @param itemBody
     */
    private void showDialog(String itemTitle, String itemBody) {

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
        void onClick(int position, String title, String body);
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

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mItemId = itemView.findViewById(R.id.item_memo_id);
            mItemTitle = itemView.findViewById(R.id.item_memo_title);
            mItemBody = itemView.findViewById(R.id.item_memo_body);
        }
    }
}
