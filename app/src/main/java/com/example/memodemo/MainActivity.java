package com.example.memodemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import com.example.memodemo.data.Record;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageView mAddBtn;
    private RecyclerView mDataList;
    private DatabaseHelper mHelper;
    private MemoDataAdapter mMemoDataAdapter;


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
        List<Record> recordList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Record record = new Record();
            //遍历数据库中的全部内容
            int id = cursor.getInt(cursor.getColumnIndex(Constants.MEMO_ID));
            String title = cursor.getString(cursor.getColumnIndex(Constants.MEMO_TITLE));
            String body = cursor.getString(cursor.getColumnIndex(Constants.MEMO_BODY));
            Log.d(TAG, "id is " + id);
            Log.d(TAG, "title is " + title);
            Log.d(TAG, "body is " + body);
            record.setId(id);
            record.setTitleName(title);
            record.setTextBody(body);
            recordList.add(record);
            Log.d(TAG, "success");
        }
        //此时成功遍历数据库中的数据
        //将数据设置到adapter中显示出来
        mMemoDataAdapter.setData(recordList);
        Log.d(TAG, "size  is " + recordList.size());
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
            public void onClick(int position, String title, String body) {
                Toast.makeText(MainActivity.this, "clicked..." + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AmendActivity.class);
                //将数据显示在amendActivity中
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.MEMO_ID, position + 1);
                bundle.putString(Constants.MEMO_TITLE, title);
                bundle.putString(Constants.MEMO_BODY, body);
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
                record.setId(position);
                Log.d(TAG, "id is " + position);
                Log.d(TAG, "title is " + title);
                Log.d(TAG, "body is " + body);
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
        dialog.setMessage("是否删除当前记录");
        dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "删除");
                //删除数据库中的记录
                doDelete(position, record);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //停留在当前页面
                Log.d(TAG, "取消");
            }
        });
        dialog.show();
    }

    /**
     * 删除数据库中的记录
     *
     * @param position
     * @param record
     */
    public void doDelete(int position, Record record) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.MEMO_ID + "=?", new String[]{String.valueOf(record.getId() + 1)});
        db.close();
        //列表中删除该记录
        mMemoDataAdapter.removeItem(position);
        //更新数据
        mDataList.post(new Runnable() {
            @Override
            public void run() {
                mMemoDataAdapter.notifyDataSetChanged();
            }
        });
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
        mDataList.setLayoutManager(linearLayoutManager);
        mMemoDataAdapter = new MemoDataAdapter();
        mDataList.setAdapter(mMemoDataAdapter);

        //创建数据库
        mHelper = new DatabaseHelper(this);
    }
}
