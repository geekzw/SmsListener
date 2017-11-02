package com.maihaoche.sms.receiver;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.maihaoche.sms.entity.Sms;
import com.maihaoche.sms.utils.SmsDao;
import com.maihaoche.sms.utils.SmsUtil;

/**
 * Created by gujian
 * Time is 2017/11/1
 * Email is gujian@maihaoche.com
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobserver extends JobService {

    SmsDao smsDao;

    @Override
    public void onCreate() {
        super.onCreate();
        smsDao = new SmsDao(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("hehe","jobservice启动");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e("hehe","jobservice运行");
        SmsUtil.sms(this,null);
        jobFinished(params,false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
