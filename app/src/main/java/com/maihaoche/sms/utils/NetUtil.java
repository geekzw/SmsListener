package com.maihaoche.sms.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.maihaoche.sms.entity.Sms;
import com.maihaoche.sms.http.OkHttpHandler;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by gujian
 * Time is 2017/10/31
 * Email is gujian@maihaoche.com
 */

public class NetUtil {
    private static String URL = "http://ds-test-pay.haimaiche.net/msmNotify/add";
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public static void sendToServer(final Context context, final Sms sms, final NetCallBack netCallBack){
        Gson gson = new Gson();
        SmsModel smsModel = new SmsModel(sms.getPhone(),sms.getContent(),sms.getTime());
        final String content = gson.toJson(smsModel);
        RequestBody body = RequestBody.create(JSON, content);
        OkHttpClient okHttpClient = OkHttpHandler.getCommonClient();
        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(netCallBack!=null){
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            netCallBack.failed();
                        }
                    });
                }
                Log.e("hehe","网络请求失败"+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(netCallBack!=null){
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            netCallBack.success();
                        }
                    });
                }
                Log.e("hehe","网络请求成功"+response.body().string());
                new SmsDao(context).update(true,sms.getSmsId());
            }
        });


    }

    public static class SmsModel implements Serializable{

        private String phone;
        private String content;
        private String receiveTime;

        public SmsModel(String phone, String content, String receiveTime) {
            this.phone = phone;
            this.content = content;
            this.receiveTime = receiveTime;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getReceiveTime() {
            return receiveTime;
        }

        public void setReceiveTime(String receiveTime) {
            this.receiveTime = receiveTime;
        }
    }

    public interface NetCallBack{
        void success();
        void failed();
    }
}
