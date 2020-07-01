package com.example.memodemo;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.memodemo.Database.DatabaseHelper;
import com.example.memodemo.Utils.Constants;
import com.example.memodemo.Utils.ImagePickerConfig;
import com.example.memodemo.domain.ImageItem;
import com.example.memodemo.domain.Record;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.example.memodemo.Utils.MyTimeFormat.getTimeStr;


public class AmendActivity extends AppCompatActivity implements ImagePickerConfig.OnImageSelectedFinishLisenter {

    private static final String TAG = "AmendActivity";
    private static final int PERMISSION_REQUEST_CODE = 2;
    private static final int MAX_SELECTED_COUNT = 3;
    private static final int PREMISSION_REQUEST_CODE = 1;
    private Calendar mCalendar;
    private int mSecond;
    private int mMinute;
    private int mHour;
    private int mDay;
    private int mMonth;
    private int mYear;
    private ImageView mBackBtn;
    private TextView mCurrentTimeTv;
    private EditText mTitleEv;
    private EditText mContentEv;
    private CheckBox mCheckBox;
    private TextView mTimePickerTv;
    private TextView mDatePickerTv;
    private Button mSaveBtn;
    private Button mClearBtn;
    private String mEditDate;
    private DatabaseHelper mHelper;

    private DatePickerDialog dialogDate;
    private TimePickerDialog dialogTime;
    private Integer year;
    private Integer month;
    private Integer dayOfMonth;
    private Integer hour;
    private Integer minute;

    private int mMyHourOfDay;
    private int mMyMinute;
    private int mMyDayOfMonth;
    private int mMyYear;
    private int mMyMonth;
    private boolean mCurrentCheckResult;
    private Record mRecord;
    private boolean mIsNeedTips;
    private boolean isChanged = false;
    private View mInsertImageBtn;
    private ImageView mPicOne;
    private ImageView mPicTwo;
    private ImageView mPicThree;
    private ImagePickerConfig mPickerConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amend);

        checkCalendarPremission();
        checkImagePremission();
        initConfig();
        getDate();
        initView();
        initData();
        initEvent();

    }

    /**
     * 检查是否获取相册的读写权限
     * 安卓6.0以上需要动态获取权限
     */
    private void checkImagePremission() {
        int readExStroagePresmission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.d(TAG, "readExStroagePresmission" + readExStroagePresmission);
        if (readExStroagePresmission == PackageManager.PERMISSION_GRANTED) {
            //有权限
        } else {
            //没有权限，需要去申请权限
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PREMISSION_REQUEST_CODE);
        }
    }

    private void initConfig() {
        mPickerConfig = ImagePickerConfig.getInstance();
        mPickerConfig.setMaxSelectedCount(MAX_SELECTED_COUNT);
        mPickerConfig.setOnImageSelectedFinishLisenter(this);
    }


    private void initEvent() {
        //返回
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入内容
                String title = mTitleEv.getText().toString().trim();
                String body = mContentEv.getText().toString().trim();
                if (!title.equals("") && !body.equals("")) {
                    //如果标题，内容都不为空，则显示dialog
                    showBackDialog(title, body);
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
                String body = mContentEv.getText().toString().trim();
                Log.d(TAG, "title is " + title);
                Log.d(TAG, "body is " + body);
                if (canDoSave(title, body)) {
                    //todo
                    updateDb(title, body);
                    //    doCalendarEvent();
                    startIntent();
                }
            }
        });

        //清空
        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentEv.setText(" ");
            }
        });


        //监听checkBox的变化
        //前提是要有改变，如果无改变，根本不会调用到该方法
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //当前状态是否被改变了
                isChanged = true;
                mCurrentCheckResult = isChecked;
                Log.d(TAG, "isChanged" + isChanged);
                Log.d(TAG, "mCurrentCheckResult" + mCurrentCheckResult);
                if (mCurrentCheckResult) {
                    //如果当前被选中
                    mDatePickerTv.setVisibility(View.VISIBLE);
                    mTimePickerTv.setVisibility(View.VISIBLE);
                } else {
                    //如果当前没被选中
                    mDatePickerTv.setVisibility(View.GONE);
                    mTimePickerTv.setVisibility(View.GONE);
                }
            }
        });


        //选择事件日期
        mDatePickerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AmendActivity.this, "1111", Toast.LENGTH_SHORT).show();
                //选择事件时间
                showDatePickerDialog();
            }
        });

        //选择提醒时间
        mTimePickerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择时间
                showTimePickerDialog();
            }
        });

        //插入图片
        mInsertImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AmendActivity.this, PickerActivity.class));
            }
        });
    }

    /**
     * 指定日历的相关操作，增删改
     */
    private void doCalendarEvent() {
        if (isChanged) {
            //如果状态改变了
            if (mCurrentCheckResult) {
                //当前选中了，insert
                Log.d(TAG, "do insert");
            } else {
                //当前没选中，delete
                Log.d(TAG, "do delete");
            }
        } else {
            //如果状态没改变
            if (mIsNeedTips) {
                //需要事件提醒,update
                Log.d(TAG, "do update");
            } else {
                //不需要事件提醒,nothing
                Log.d(TAG, "do nothing");
            }
        }
    }

    /**
     * 设置提醒事件
     */
    private void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mMyHourOfDay = hourOfDay;
                mMyMinute = minute;
                mTimePickerTv.setText(hourOfDay + ":" + minute);
            }
        };
        TimePickerDialog dialog = new TimePickerDialog(this, 0, listener, mHour, mMinute, true);
        dialog.show();
    }


    /**
     * 设置事件日期
     */
    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //显示设置的回显
                mMyYear = year;
                mMyMonth = ++month;
                mMyDayOfMonth = dayOfMonth;
                mDatePickerTv.setText(year + "-" + mMyMonth + "-" + dayOfMonth);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, 0, listener, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    /**
     * 能否保存
     *
     * @param title
     * @param body
     * @return
     */
    private boolean canDoSave(String title, String body) {
        //用于判断能否进行保存动作
        boolean flag = true;
        if (title.equals("")) {
            Toast.makeText(this, R.string.title_not_null, Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (title.length() > 10) {
            Toast.makeText(this, R.string.title_too_long, Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (body.length() > 200) {
            Toast.makeText(this, R.string.body_too_long, Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (body.equals("")) {
            Toast.makeText(this, R.string.body_not_null, Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (mCurrentCheckResult) {
            //如果选中“添加提醒事件”,就需要将日期时间填写完整才可以
            if (mDatePickerTv.getText().toString().equals(getString(R.string.picker_date)) || mTimePickerTv.getText().toString().equals(getString(R.string.picker_time))) {
                Toast.makeText(this, R.string.date_or_time_not_null, Toast.LENGTH_SHORT).show();
                flag = false;
            }
        }
        //true:此时说明可以保存内容,保存内容至数据库中
        //flase:不能保存，需要写内容
        return flag;
    }

    /**
     * 显示dialog
     *
     * @param title
     * @param body
     */
    private void showBackDialog(final String title, final String body) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AmendActivity.this);
        dialog.setTitle("提示：");
        dialog.setMessage("是否保存当前内容");
        dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "保存");
                //将数据更新至数据库中，并设置提醒事件
                if (canDoSave(title, body)) {
                    //todo
                    updateDb(title, body);
                    //    doCalendarEvent();
                    startIntent();
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //跳转至首页
                Log.d(TAG, "取消");
                startIntent();
            }
        });
        dialog.show();
    }

    /**
     * 修改内容保存到数据库中
     *
     * @param title
     * @param body
     */
    private void updateDb(String title, String body) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.MEMO_TITLE, title);
        values.put(Constants.MEMO_BODY, body);
        values.put(Constants.MEMO_MODIFY_TIME, mEditDate);
        values.put(Constants.MEMO_NEED_TIPS, mCurrentCheckResult);
        Log.d(TAG, "mCurrentCheckResult" + mCurrentCheckResult);
        db.update(Constants.TABLE_NAME, values, Constants.MEMO_ID + "=?", new String[]{mRecord.getId().toString()});
        Toast.makeText(this, "修改成功！", Toast.LENGTH_SHORT).show();
        db.close();
    }

    /**
     * 从mainactivity点击到该界面，将数据回显
     */
    private void initData() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        //获取bundle中的数据
        if (bundle != null) {
            String memoTitle = intent.getStringExtra(Constants.MEMO_TITLE);
            String memoContent = intent.getStringExtra(Constants.MEMO_BODY);
            int memoId = intent.getIntExtra(Constants.MEMO_ID, 0);
            mIsNeedTips = intent.getBooleanExtra(Constants.MEMO_NEED_TIPS, false);

            //设置数据
            mRecord = new Record();
            mRecord.setTitleName(memoTitle);
            mRecord.setTextBody(memoContent);
            mRecord.setId(memoId);
            mRecord.setTipsChecked(mIsNeedTips);
            if (mIsNeedTips) {
                //如果设置了提醒事件，就将日期和时间显示出来
                mDatePickerTv.setVisibility(View.VISIBLE);
                mTimePickerTv.setVisibility(View.VISIBLE);
                mCheckBox.setChecked(mIsNeedTips);
                //获取到calendar中的日期与时间
                setDateAndTime(memoTitle);
            }
            mContentEv.setText(memoContent);
            mTitleEv.setText(memoTitle);

        }

    }

    /**
     * 根据title
     * 获取到初次设置提醒的日期和时间
     *
     * @param memoTitle
     */
    private void setDateAndTime(String memoTitle) {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://com.android.calendar/events");
        Cursor cursor = contentResolver.query(uri, new String[]{CalendarContract.Events.DTSTART}, CalendarContract.Events.TITLE + "=" + memoTitle, null, null, null);
        String[] columnNames = cursor.getColumnNames();
        while (cursor.moveToNext()) {
            for (String columnName : columnNames) {
                String startTimeStr = cursor.getString(cursor.getColumnIndex(columnName));
                //将秒数转化为日期
                getTipsDate(startTimeStr);
                Log.d(TAG, columnName + "==" + startTimeStr);
            }
        }
    }

    /**
     * 通过数据库的中的设置时间获取提醒事件的时间
     *
     * @param startTimeStr
     */
    private void getTipsDate(String startTimeStr) {
        long time = Long.parseLong(startTimeStr);
        Date date = new Date(time);
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String currentTime = dateFormat.format(gregorianCalendar.getTime());
        Log.d(TAG, "currentTime is " + currentTime);

        //将日期分割成 date + time 的格式
        //将 2020-07-26 10:43:03 划分为
        //2020-07-26 和 10:43:03
        String[] dateAndTime = currentTime.split(" ");
        String tipsDate = dateAndTime[0];
        String tipsTime = dateAndTime[1];
        Log.d(TAG, "tipsDate" + tipsDate);
        Log.d(TAG, "tipsTime" + tipsTime);
        //
        mDatePickerTv.setText(tipsDate);
        mTimePickerTv.setText(tipsTime);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mBackBtn = findViewById(R.id.amend_back_btn);
        mCurrentTimeTv = findViewById(R.id.amend_memo_current_edit_time);
        mTitleEv = findViewById(R.id.amend_memo_title);
        mContentEv = findViewById(R.id.amend_content);
        mCheckBox = findViewById(R.id.amend_memo_is_tip);
        mDatePickerTv = findViewById(R.id.amend_memo_check_tip_date);
        mTimePickerTv = findViewById(R.id.amend_memo_check_tip_time);
        mSaveBtn = findViewById(R.id.amend_save_btn);
        mClearBtn = findViewById(R.id.amend_clean_btn);
        mInsertImageBtn = findViewById(R.id.amend_insert_image_btn);
        mPicOne = findViewById(R.id.amend_pic_one);
        mPicTwo = findViewById(R.id.amend_pic_two);
        mPicThree = findViewById(R.id.amend_pic_three);

        //初始化当前时间
        Date date = new Date(System.currentTimeMillis());
        //创建的时间，存入数据库，数据库根据时间先后显示列表
        mEditDate = getTimeStr(date);

        if (mCurrentTimeTv.getText().length() != 0) {
            mCurrentTimeTv.setText(mEditDate);
        }

        dialogDate = null;
        dialogTime = null;
        hour = 0;
        minute = 0;
        year = 0;
        month = 0;
        dayOfMonth = 0;

        //初始化数据库
        mHelper = new DatabaseHelper(this);

        //获取到之前选择的图片并显示出来
        List<ImageItem> selectResult = mPickerConfig.getSelectResult();
        int seletcedSize = mPickerConfig.getSeletcedSize();
        if (selectResult != null) {
            for (ImageItem imageItem : selectResult) {
                Log.d(TAG, "image item is " + imageItem.getPath());
            }
            Log.d(TAG, "selected size is " + seletcedSize);
        }
        if (selectResult != null) {
            //显示图片
            switch (seletcedSize) {
                case 1:
                    Glide.with(mPicOne.getContext()).load(selectResult.get(0).getPath()).into(mPicOne);
                    mPicOne.setVisibility(View.VISIBLE);
                    mPicTwo.setVisibility(View.GONE);
                    mPicThree.setVisibility(View.GONE);
                    break;
                case 2:
                    Glide.with(mPicOne.getContext()).load(selectResult.get(0).getPath()).into(mPicOne);
                    Glide.with(mPicTwo.getContext()).load(selectResult.get(1).getPath()).into(mPicTwo);
                    mPicOne.setVisibility(View.VISIBLE);
                    mPicTwo.setVisibility(View.VISIBLE);
                    mPicThree.setVisibility(View.GONE);
                    break;
                case 3:
                    Glide.with(mPicOne.getContext()).load(selectResult.get(0).getPath()).into(mPicOne);
                    Glide.with(mPicTwo.getContext()).load(selectResult.get(1).getPath()).into(mPicTwo);
                    Glide.with(mPicThree.getContext()).load(selectResult.get(2).getPath()).into(mPicThree);
                    mPicOne.setVisibility(View.VISIBLE);
                    mPicTwo.setVisibility(View.VISIBLE);
                    mPicThree.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            mPicOne.setVisibility(View.GONE);
            mPicTwo.setVisibility(View.GONE);
            mPicThree.setVisibility(View.GONE);
        }
    }

    /**
     * 页面跳转
     */
    private void startIntent() {
        Intent intent = new Intent(AmendActivity.this, MainActivity.class);
        startActivity(intent);
    }


    /**
     * 检查是否获取日历的读写权限
     * 安卓6.0以上需要动态获取权限
     */
    private void checkCalendarPremission() {
        int writePremission = checkSelfPermission(Manifest.permission.WRITE_CALENDAR);
        int readPremission = checkSelfPermission(Manifest.permission.READ_CALENDAR);
        if (writePremission == PackageManager.PERMISSION_GRANTED && readPremission == PackageManager.PERMISSION_GRANTED) {
            //有读写日历的权限
            Log.d(TAG, "has premission...");
        } else {
            //没有权限
            //需要获取权限
            Log.d(TAG, "no premission...");
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * 判断请求码的结果来决定是否能成功获取权限
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            //权限检查的结果
            if (grantResults.length == 3 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                //此时有权限
                Toast.makeText(this, "有权限", Toast.LENGTH_SHORT).show();
            } else {
                //此时没有权限
                //可以给出一个提示：
                //用户点击确定后重新调用请求权限
                //用户点击取消后就不再获取权限了
                finish();
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 获取当前时间
     * 年月日时分秒
     */
    private void getDate() {
        mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        //获取到的月是从0开始的！！！
        mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mHour = mCalendar.get(java.util.Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(java.util.Calendar.MINUTE);
        mSecond = mCalendar.get(java.util.Calendar.SECOND);
    }

    @Override
    public void onSelectedFinish(List<ImageItem> selectedResult) {
        //所选择的图片列表在该处回来了
        //设置图片
        if (selectedResult != null) {
            setPics(selectedResult);
        }
    }

    private void setPics(List<ImageItem> selectedResult) {
        switch (selectedResult.size()) {
            case 1:
                Glide.with(mPicOne.getContext()).load(selectedResult.get(0).getPath()).into(mPicOne);
                mPicOne.setVisibility(View.VISIBLE);
                mPicTwo.setVisibility(View.GONE);
                mPicThree.setVisibility(View.GONE);
                break;
            case 2:
                Glide.with(mPicOne.getContext()).load(selectedResult.get(0).getPath()).into(mPicOne);
                Glide.with(mPicTwo.getContext()).load(selectedResult.get(1).getPath()).into(mPicTwo);
                mPicOne.setVisibility(View.VISIBLE);
                mPicTwo.setVisibility(View.VISIBLE);
                mPicThree.setVisibility(View.GONE);
                break;
            case 3:
                Glide.with(mPicOne.getContext()).load(selectedResult.get(0).getPath()).into(mPicOne);
                Glide.with(mPicTwo.getContext()).load(selectedResult.get(1).getPath()).into(mPicTwo);
                Glide.with(mPicThree.getContext()).load(selectedResult.get(2).getPath()).into(mPicThree);
                mPicOne.setVisibility(View.VISIBLE);
                mPicTwo.setVisibility(View.VISIBLE);
                mPicThree.setVisibility(View.VISIBLE);
                break;
        }
    }

}