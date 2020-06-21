package com.example.memodemo;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memodemo.Database.DatabaseHelper;
import com.example.memodemo.Utils.Constants;
import com.example.memodemo.data.Record;

public class AmendActivity extends AppCompatActivity {

    private static final String TAG = "AmendActivity";
    private ImageView mBackBtn;
    private EditText mMemoEv;
    private EditText mTitleEv;
    private Button mSaveBtn;
    private Button mCleanBtn;
    private DatabaseHelper mHelper;
    private Record mRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amend);

        initView();
        initData();
        initEvent();
    }

    /**
     * 点击事件
     */
    private void initEvent() {
        //返回
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入内容
                String title = mTitleEv.getText().toString().trim();
                String body = mMemoEv.getText().toString().trim();
                if (!title.equals("") && !body.equals("")) {
                    //如果标题，内容都不为空，则显示dialog
                    showDialog(title, body);
                } else {
                    //如果都为空则返回
                    startIntent();
                }
            }
        });

        //保存
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入内容
                String title = mTitleEv.getText().toString().trim();
                String body = mMemoEv.getText().toString().trim();
                Log.d(TAG, "title is " + title);
                Log.d(TAG, "body is " + body);
                if (canDoSave(title, body)) {
                    Toast.makeText(AmendActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    updataDb(title, body);
                    startIntent();
                }
            }
        });

        //清空
        mCleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMemoEv.setText(" ");
            }
        });
    }

    /**
     * 修改内容保存到数据库中
     * @param title
     * @param body
     */
    private void updataDb(String title, String body) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.MEMO_TITLE, title);
        values.put(Constants.MEMO_BODY, body);
        db.update(Constants.TABLE_NAME,  values,Constants.MEMO_ID+"=?",new String[]{mRecord.getId().toString()});
        Toast.makeText(this, "修改成功！", Toast.LENGTH_SHORT).show();
        db.close();
    }

    /**
     * 能否保存
     * @param title
     * @param body
     * @return
     */
    private boolean canDoSave(String title, String body) {
        //用于判断能否进行保存动作
        boolean flag = true;
        if (title.equals("")) {
            Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (title.length() > 10) {
            Toast.makeText(this, "标题过长", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (body.length() > 200) {
            Toast.makeText(this, "内容过长", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (body.equals("")) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        //true:此时说明可以保存内容,保存内容至数据库中
        //flase:不能保存，需要写内容
        return flag;
    }


    /**
     * 显示dialog
     * @param title
     * @param body
     */
    private void showDialog(final String title, final String body) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AmendActivity.this);
        dialog.setTitle("提示：");
        dialog.setMessage("是否保存当前内容");
        dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "保存");
                updataDb(title, body);
                startIntent();
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
     * 页面跳转
     */
    private void startIntent() {
        Intent intent = new Intent(AmendActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mBackBtn = findViewById(R.id.amend_back_btn);
        mTitleEv = findViewById(R.id.amend_memo_title);
        mMemoEv = findViewById(R.id.amend_content);
        mSaveBtn = findViewById(R.id.amend_save_btn);
        mCleanBtn = findViewById(R.id.amend_clean_btn);

        //初始化数据库
        mHelper = new DatabaseHelper(this);
    }

    /**
     * 如果从mainactivity点击到该界面的的话
     * 就要将数据显示出来
     */
    private void initData() {
        //获取intent
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        //获取intent中的数据
        if (bundle != null) {
            mRecord = new Record();
            String memoTitle = intent.getStringExtra(Constants.MEMO_TITLE);
            String memoBody = intent.getStringExtra(Constants.MEMO_BODY);
            int memoId = intent.getIntExtra(Constants.MEMO_ID, 0);
            Log.d(TAG, "title is" + memoTitle);
            Log.d(TAG, "memoBody is" + memoBody);
            //设置数据
            mRecord.setTextBody(memoBody);
            mRecord.setTitleName(memoTitle);
            mRecord.setId(memoId);
            mMemoEv.setText(memoBody);
            mTitleEv.setText(memoTitle);
        }
    }
}
