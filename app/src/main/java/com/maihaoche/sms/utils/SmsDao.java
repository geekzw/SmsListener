package com.maihaoche.sms.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.maihaoche.sms.entity.Sms;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujian
 * Time is 2017/10/30
 * Email is gujian@maihaoche.com
 */

public class SmsDao {

    public static final String ID="Id";
    public static final String PHONE="phone";
    public static final String CONTENT="content";
    public static final String TIME="time";
    public static final String SERVER="server";
    public static final String SMS_ID="smsId";
    public static final String IS_DELETE="isDelete";

    private Context context;
    private DatabaseUtil databaseUtil;
    private SQLiteDatabase db;

    public SmsDao(Context context) {
        this.context = context;
        databaseUtil = new DatabaseUtil(context);
    }

    public long insert(Sms sms){

        db = databaseUtil.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PHONE, sms.getPhone());
        contentValues.put(CONTENT, sms.getContent());
        contentValues.put(TIME, sms.getTime());
        contentValues.put(SERVER, sms.getIsSendToServer());
        contentValues.put(SMS_ID, sms.getSmsId());
        contentValues.put(IS_DELETE, sms.getDelete());
        long index = db.insertOrThrow(DatabaseUtil.TABLE_NAME, null, contentValues);
        return index;
    }

    public List<Sms> select(Integer limit,Integer offset){
        String sql = "select * from sms where isDelete=1 order by Id desc limit ? offset ?";
        String[] params = new String[]{limit+"",offset+""};
        db = databaseUtil.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,params);
        List<Sms> lists = new ArrayList<>();
        String phone;
        String content;
        String time;
        int smsId;
        int server;
        int id;
        if (cursor.moveToFirst()) {
            do {

                phone = cursor.getString(cursor.getColumnIndex(PHONE));
                content = cursor.getString(cursor.getColumnIndex(CONTENT));
                time = cursor.getString(cursor.getColumnIndex(TIME));
                server = cursor.getInt(cursor.getColumnIndex(SERVER));
                smsId = cursor.getInt(cursor.getColumnIndex(SMS_ID));
                id = cursor.getInt(cursor.getColumnIndex(ID));

                Sms sms = new Sms(phone,content,time);
                sms.setIsSendToServer(server);
                sms.setSmsId(smsId);
                sms.setId(id);
                lists.add(sms);
            }while (cursor.moveToNext());

        }
        return lists;
    }

    public void update(boolean isToServer,int smsId){
        int server = isToServer?1:0;
        String sql = "update sms set server=? where smsId=?";
        String[] params = new String[]{server+"",smsId+""};
        db = databaseUtil.getWritableDatabase();
        db.execSQL(sql,params);
    }

    public void delete(int smsId){
        String sql = "update sms set isDelete=0 where smsId=?";
        String[] params = new String[]{smsId+""};
        db = databaseUtil.getWritableDatabase();
        db.execSQL(sql,params);
    }

    public void deleteBatch(List<Integer> smsIds){
        String sql = "update sms set isDelete=0 where Id=?";
        String[] params;
        db = databaseUtil.getWritableDatabase();
        db.beginTransaction();
        for(Integer i:smsIds){
            params = new String[]{i.intValue()+""};
            db.execSQL(sql,params);
        }
        db.setTransactionSuccessful();
        db.endTransaction();

    }

}
