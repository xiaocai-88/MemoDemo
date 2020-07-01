package com.example.memodemo.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.memodemo.Utils.Constants;

/**
 * description 创建数据库
 * create by xiaocai on 2020/6/12
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    /**
     * 构造方法
     *
     * @param context
     */
    public DatabaseHelper(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.VERSION);
    }

    /**
     * 创建数据库
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "创建数据库");
        String createSql = "create table " + Constants.TABLE_NAME
                + " ( " + Constants.MEMO_ID + " integer primary key autoincrement, "
                + Constants.MEMO_TITLE + " varchar(20), "
                + Constants.MEMO_BODY + " varchar(150), "
                + Constants.MEMO_CREEATE_TIME + " DATETIME NOT NULL,"
                + Constants.MEMO_MODIFY_TIME + " DATETIME,"
                + Constants.MEMO_NEED_TIPS + " boolean)";
        db.execSQL(createSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
