package com.example.memodemo.Utils;

/**
 * description 存放常量
 * create by xiaocai on 2020/6/11
 */
public class Constants {
    //数据库名
    public static final String DATABASE_NAME = "test.db";
    //数据库版本
    public static final int VERSION = 1;
    //数据表名
    public static final String TABLE_NAME = "memo";
    //id
    public static final String MEMO_ID = "_id";
    //title
    public static final String MEMO_TITLE = "title";
    //mome
    public static final String MEMO_BODY = "body";
    //创建时间
    public static final String MEMO_CREEATE_TIME = "create_time";
    //修改时间
    public static final String MEMO_MODIFY_TIME = "memo_modify_time";
    //是否添加了提醒
    //此处的值是0和1
    //0：没有提醒
    public static final String MEMO_NEED_TIPS = "memo_need_tips";

}
