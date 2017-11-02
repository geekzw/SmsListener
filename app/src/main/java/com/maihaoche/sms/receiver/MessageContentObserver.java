package com.maihaoche.sms.receiver;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import com.maihaoche.sms.entity.Sms;
import com.maihaoche.sms.utils.NetUtil;
import com.maihaoche.sms.utils.SmsDao;
import com.maihaoche.sms.utils.SmsUtil;

/**
 * Created by gujian
 * Time is 2017/10/30
 * Email is gujian@maihaoche.com
 */

public class MessageContentObserver extends ContentObserver {

    private Context mContext;
    private SmsDao smsDao;

    public MessageContentObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        smsDao = new SmsDao(context);
    }

    /**
     * 回调函数, 当监听的Uri发生改变时，会回调该方法
     * 需要注意的是当收到短信的时候会回调两次
     * 收到短信一般来说都是执行了两次onchange方法.第一次一般都是raw的这个.
     * 虽然收到了短信.但是短信并没有写入到收件箱里
     */
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        SmsUtil.sms(mContext,uri);
    }

}
