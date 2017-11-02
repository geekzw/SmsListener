package com.maihaoche.sms.receiver;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.maihaoche.sms.MainActivity;
import com.maihaoche.sms.R;

/**
 * Created by gujian
 * Time is 2017/10/30
 * Email is gujian@maihaoche.com
 */

public class MyService  extends Service {

    private MessageContentObserver mObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "短信监听服务器已启动....", Toast.LENGTH_LONG).show();
        // 在这里启动
        ContentResolver resolver = getContentResolver();
        mObserver = new MessageContentObserver(getBaseContext(), new SmsHandler(this));
        resolver.registerContentObserver(Uri.parse("content://sms/"), true,mObserver);
        //定义一个notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        //设置小图标
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        //设置标题
        mBuilder.setContentTitle("短信拦截");
        //设置通知正文
        mBuilder.setContentText("拦截分析中，请勿关闭...");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        mBuilder.setContentIntent(pendingIntent);

        //把该service创建为前台service
        startForeground(1,mBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
