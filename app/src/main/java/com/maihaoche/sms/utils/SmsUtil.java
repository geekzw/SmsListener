package com.maihaoche.sms.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.maihaoche.sms.entity.Sms;

/**
 * Created by gujian
 * Time is 2017/11/1
 * Email is gujian@maihaoche.com
 */

public class SmsUtil {

    public static void sms(Context context,Uri uri){
        SmsDao smsDao = new SmsDao(context);
        if (uri!=null && uri.toString().equals("content://sms/raw")) {
            return;
        }
        Uri inboxUri = Uri.parse("content://sms/inbox");
        Cursor c = context.getContentResolver().query(inboxUri, null, "read=0", null, "date desc");  // 按时间顺序排序短信数据库
        if (c != null) {
            if (c.moveToFirst()) {

                do {
                    int id = c.getInt(c.getColumnIndex("_id"));
                    int readIndex = c.getColumnIndex("read");
                    int read = c.getInt(readIndex);
                    if (read ==0) {
                        setreaded(context,id);
                        String address = c.getString(c.getColumnIndex("address"));//发送方号码
                        String body = c.getString(c.getColumnIndex("body")); // 短信内容

                        Sms sms = new Sms(address, body);
                        sms.setSmsId(id);
                        smsDao.insert(sms);
                        Toast.makeText(context, "插入成功:" + address + body, Toast.LENGTH_SHORT).show();
                        NetUtil.sendToServer(context,sms,null);
                    }
                }while (c.moveToNext());



            }
            c.close();
        }
    }

    private static void setreaded(Context context,int id) {
        ContentValues values = new ContentValues();
        //修改短信为已读模式
        values.put("read", "1");
        context.getContentResolver().update(Uri.parse("content://sms/inbox"), values, " _id=?", new String[]{"" + id});
    }
}
