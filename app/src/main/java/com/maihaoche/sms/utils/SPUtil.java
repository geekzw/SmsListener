package com.maihaoche.sms.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.maihaoche.sms.MyApp;

/**
 * Created by gujian
 * Time is 2017/10/30
 * Email is gujian@maihaoche.com
 */

public class SPUtil {

    private static final String SETTING = "setting";

    private static final String PHONE = "phone";

    private static final String TEXT = "text";

    public static void save(String phone, String text){

        SharedPreferences settings = MyApp.getInstence().getSharedPreferences(SETTING, 0);
        SharedPreferences.Editor editor = settings.edit();
    }
}
