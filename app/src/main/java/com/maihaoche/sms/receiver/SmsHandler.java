package com.maihaoche.sms.receiver;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * @author Javen
 *
 *         短信的处理
 *
 */
public class SmsHandler extends Handler {
    private Context mcontext;

    public SmsHandler(Context context) {
        this.mcontext = context;
    }

    @Override
    public void handleMessage(Message msg) {

    }
}