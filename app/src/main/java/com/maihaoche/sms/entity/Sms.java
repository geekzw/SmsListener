package com.maihaoche.sms.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gujian
 * Time is 2017/10/30
 * Email is gujian@maihaoche.com
 */

public class Sms {

    private int id;

    private String phone;

    private String content;

    private String time;

    /**
     * 0为未上传，1为上传
     */
    private int isSendToServer=0;

    private int smsId;
    /**
     * 0为删除，1为正常
     */
    private int delete = 1;

    private boolean isSelect = false;

    public Sms(String phone, String content, String time) {
        this.phone = phone;
        this.content = content;
        this.time = time;
    }

    public Sms(String phone, String content) {
        this.phone = phone;
        this.content = content;
        time = System.currentTimeMillis()+"";
        isSendToServer = 0;
    }

    public Sms(int id, String phone, String content) {
        this.id = id;
        this.phone = phone;
        this.content = content;
        isSendToServer = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIsSendToServer() {
        return isSendToServer;
    }


    public void setIsSendToServer(int isSendToServer) {
        this.isSendToServer = isSendToServer;
    }

    public boolean isSendToServer(){
        return isSendToServer==1;
    }

    public int getSmsId() {
        return smsId;
    }


    public void setSmsId(int smsId) {
        this.smsId = smsId;
    }

    public int getDelete() {
        return delete;
    }

    public void setDelete(int delete) {
        this.delete = delete;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getFormatData(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(Long.valueOf(time)));
    }
}
