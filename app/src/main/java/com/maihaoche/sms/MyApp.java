package com.maihaoche.sms;

import android.app.Application;

/**
 * Created by gujian
 * Time is 2017/10/24
 * Email is gujian@maihaoche.com
 */

public class MyApp extends Application {

    private static MyApp myApp;

    @Override
    public void onCreate() {
        super.onCreate();

        myApp = this;

    }

    public static MyApp getInstence(){

        return myApp;
    }
}
