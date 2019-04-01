package com.example.myapplication;

import android.graphics.drawable.Drawable;

public class TrafficInfo {
    private Drawable icon;
    private String appname;
    private String packname;
    private int uid;


    public TrafficInfo() {
        super();
    }

    public TrafficInfo(Drawable icon, String appname, String packname, int uid) {
        super();
        this.icon = icon;
        this.appname = appname;
        this.packname = packname;
        this.uid = uid;
    }

    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public String getAppname() {
        return appname;
    }
    public void setAppname(String appname) {
        this.appname = appname;
    }
    public String getPackname() {
        return packname;
    }
    public void setPackname(String packname) {
        this.packname = packname;
    }
    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "TrafficInfo [icon=" + icon + ", appname=" + appname
                + ", packname=" + packname + ", uid=" + uid + "]";
    }
}