package com.example.memodemo;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memodemo.Adapter.MemoDataAdapter;
import com.example.memodemo.Database.DatabaseHelper;
import com.example.memodemo.Utils.Constants;
import com.example.memodemo.domain.Record;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageView mAddBtn;
    private RecyclerView mDataList;
    private DatabaseHelper mHelper;
    private MemoDataAdapter mMemoDataAdapter;
    private String mTitle;
    private String mBody;
    private String mCreateTime;

    private ArrayList<Integer> memoId = new ArrayList<>();
    private String mValues;
    private String mModifyTime;
    private String mIsTipsChecked;
    private List<Record> mRecordList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initEvent();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //获取数据库中的内容
        mRecordList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //显示的是创建的时间
        //按编辑时间进行排序
        Cursor cursor = db.query(Constants.TABLE_NAME, null, null, null, null, null, "memo_modify_time " + "asc");
        while (cursor.moveToNext()) {
            Record record = new Record();
            //遍历数据库中的全部内容
            int id = cursor.getInt(cursor.getColumnIndex(Constants.MEMO_ID));
            mTitle = cursor.getString(cursor.getColumnIndex(Constants.MEMO_TITLE));
            mBody = cursor.getString(cursor.getColumnIndex(Constants.MEMO_BODY));
            mCreateTime = cursor.getString(cursor.getColumnIndex(Constants.MEMO_CREEATE_TIME));
            mModifyTime = cursor.getString(cursor.getColumnIndex(Constants.MEMO_MODIFY_TIME));
            mIsTipsChecked = cursor.getString(cursor.getColumnIndex(Constants.MEMO_NEED_TIPS));
            Log.d(TAG, "id is " + id);
            Log.d(TAG, "title is " + mTitle);
            Log.d(TAG, "body is " + mBody);
            Log.d(TAG, "mModifyTime is " + mModifyTime);
            //此处获取是否设置提醒获得的是0和1,需要转换为true和false
            //0：没有提醒
            Log.d(TAG, "mIsTipsChecked is " + mIsTipsChecked);
            //将日期换为秒
            Log.d(TAG, "createTime is " + mCreateTime);
            record.setId(id);
            //将数据保存至集合中
            memoId.add(id);

            record.setTitleName(mTitle);
            record.setTextBody(mBody);
            record.setCreateTime(mCreateTime);
            record.setModifyTime(mModifyTime);
            if (mIsTipsChecked.equals("1")) {
                record.setTipsChecked(true);
            } else {
                record.setTipsChecked(false);
            }
            mRecordList.add(record);
            Log.d(TAG, "initData success...");
        }
        //此时成功遍历数据库中的数据
        //将数据设置到adapter中显示出来
        mMemoDataAdapter.setData(mRecordList);
//        Log.d(TAG, "size  is " + recordList.size());
        cursor.close();
        db.close();
    }

    /**
     * 设置点击事件
     */
    private void initEvent() {
        //点击按钮跳转至编辑界面
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        //点击item跳转到编辑界面
        mMemoDataAdapter.setOnItemClickLinstener(new MemoDataAdapter.OnItemClickLinstener() {
            @Override
            public void onClick(int position, String title, String body, boolean tipsChecked) {
                Toast.makeText(MainActivity.this, "clicked..." + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AmendActivity.class);
                //将数据显示在amendActivity中
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.MEMO_ID, position + 1);
                bundle.putString(Constants.MEMO_TITLE, title);
                bundle.putString(Constants.MEMO_BODY, body);
                bundle.putBoolean(Constants.MEMO_NEED_TIPS, tipsChecked);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //长按item删除
        mMemoDataAdapter.setOnItemLongClickLinstener(new MemoDataAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(int position, String title, String body) {
                Toast.makeText(MainActivity.this, "long click", Toast.LENGTH_SHORT).show();
                //提示dialog
                Record record = new Record();
                record.setTitleName(title);
                record.setTextBody(body);
                Integer currentId = memoId.get(position);
                record.setId(currentId);

//                Log.d(TAG, "id is " + position);
//                Log.d(TAG, "title is " + title);
//                Log.d(TAG, "body is " + body);
                showDialog(position, record);
            }
        });
    }

    /**
     * 提示dialog是否删除该记录
     *
     * @param position
     * @param record
     */
    private void showDialog(final int position, final Record record) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("提示：");
        dialog.setMessage("是否删除当前记录(添加的提醒事件将会同时被删除)");
        dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //删除数据库中的记录
                doDelete(position, record);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //停留在当前页面
                Toast.makeText(MainActivity.this, "已取消删除", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }


    /**
     * 删除数据库中的记录
     * 同时删除提醒事件
     *
     * @param position list中所在的位置
     * @param record   record.getId():备忘录的id
     */
    public void doDelete(int position, Record record) {
        int currentMemoId = record.getId();
        Log.d(TAG, "currente memo id  is " + currentMemoId);
        Log.d(TAG, "currente memo position  is " + position);
        //memo.db删除数据
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.MEMO_ID + "=?", new String[]{String.valueOf(currentMemoId)});
        db.close();
        //列表中删除该记录
        mMemoDataAdapter.removeItem(position);

        //判断是否删除提醒事件
        boolean tipsChecked = record.getTipsChecked();
        Log.d(TAG, "tipsChecked" + tipsChecked);
        if (tipsChecked) {
            //删除提醒事件
            //可以获取到备忘录的title和body
            String deleteTitle = record.getTitleName();
            //根据title和body查询calendar中的id
            queryPosition(deleteTitle);
        }

        //更新数据/ui
        mDataList.post(new Runnable() {
            @Override
            public void run() {
                mMemoDataAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 通过title搜索出提醒事件的id进行删除
     * 目前只是根据title在calendar中进行查询id,但不能确保title的唯一性
     * 该功能需要进一步完善，如根据event的创建时间进行查询
     *
     * @param deleteTitle 当前删除memo的标题
     */
    private void queryPosition(String deleteTitle) {
        //遍历calendar的数据库来找到对应memo的id
        //查询事件
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://com.android.calendar/events");
        Cursor cursor = contentResolver.query(uri, new String[]{CalendarContract.Events._ID}, CalendarContract.Events.TITLE + "=" + deleteTitle, null, null, null);
        String[] columnNames = cursor.getColumnNames();
        while (cursor.moveToNext()) {
            for (String columnName : columnNames) {
                mValues = cursor.getString(cursor.getColumnIndex(columnName));
                Log.d(TAG, columnName + "==" + mValues);
            }
        }

        long deleteEventId = Integer.parseInt(mValues);
        Log.d(TAG, "deleteEventId is " + deleteEventId);
        cursor.close();
        //根据ID删除calendar表中的数据
        if (deleteEventId != 0) {
            Uri deleteEventUri = ContentUris.withAppendedId(Uri.parse("content://com.android.calendar/events"), deleteEventId);
            getContentResolver().delete(deleteEventUri, null, null);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        //添加按钮
        mAddBtn = findViewById(R.id.add_context_btn);

        //数据列表
        mDataList = findViewById(R.id.memo_list);
        //recyclerview的基本设置
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        mDataList.setLayoutManager(linearLayoutManager);
        mMemoDataAdapter = new MemoDataAdapter();
        mDataList.setAdapter(mMemoDataAdapter);

        //创建数据库
        mHelper = new DatabaseHelper(this);
    }
}
