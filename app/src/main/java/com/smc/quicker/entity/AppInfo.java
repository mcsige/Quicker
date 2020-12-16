package com.smc.quicker.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class AppInfo implements Parcelable {

    public static final String TABLE = "appinfo";// 表名
    public static final String KEY_uid = "uid";// 列名
    public static final String KEY_appName = "app_name";// 列名
    public static final String KEY_packageName = "package_name";// 列名
    public static final String KEY_times = "times";// 列名
    public static final String KEY_appOrder = "app_order";// 顺序

    private String packageName;
    private String appName;
    private int uid;
    private int times;
    private int appOrder;

    public AppInfo(){}

    public AppInfo(String packageName, String appName, int uid, int times, int appOrder) {
        this.packageName = packageName;
        this.appName = appName;
        this.uid = uid;
        this.times = times;
        this.appOrder = appOrder;
    }

    protected AppInfo(Parcel in) {
        packageName = in.readString();
        appName = in.readString();
        uid = in.readInt();
        times = in.readInt();
        appOrder = in.readInt();
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getAppOrder() {
        return appOrder;
    }

    public void setAppOrder(int appOrder) {
        this.appOrder = appOrder;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", uid=" + uid +
                ", times=" + times +
                ", appOrder=" + appOrder +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(packageName);
        dest.writeString(appName);
        dest.writeInt(uid);
        dest.writeInt(times);
        dest.writeInt(appOrder);
    }
}
