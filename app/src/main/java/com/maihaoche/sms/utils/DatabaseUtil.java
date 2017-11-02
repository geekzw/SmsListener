package com.maihaoche.sms.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by gujian
 * Time is 2017/10/30
 * Email is gujian@maihaoche.com
 */

public class DatabaseUtil extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "mySms.db";
    public static final String TABLE_NAME = "sms";
    private static final String CREATE_TABLE = "create table if not exists "+
            TABLE_NAME+"(Id integer primary key,phone text,content text,time text,server integer,smsId integer,isDelete integer)";
    private static DatabaseUtil instence;

    public static DatabaseUtil getInstence(Context context){

        if(instence == null ){
            instence = new DatabaseUtil(context);
        }
        return instence;
    }


    public DatabaseUtil(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
        Log.e("hehe","创建数据库");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }
}
