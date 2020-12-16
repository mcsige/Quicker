package com.smc.quicker.util;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.smc.quicker.entity.AppInfo;


public class DBHelper extends SQLiteOpenHelper {
    //version number to upgrade database version //each time if you Add, Edit table, you need to change the version number.
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//建立数据库
        String CREATE_TABLE_STUDENT = "CREATE TABLE " + AppInfo.TABLE  + "("
                + AppInfo.KEY_uid  + " INTEGER PRIMARY KEY ,"
                + AppInfo.KEY_appName + " TEXT, "
                + AppInfo.KEY_packageName + " TEXT, "
                + AppInfo.KEY_times + " INTEGER, "
                + AppInfo.KEY_appOrder + " INTEGER )";
        db.execSQL(CREATE_TABLE_STUDENT);
    }

    public Cursor onList(SQLiteDatabase db) {//查询全部数据
        String selectQuery = "SELECT "+AppInfo.KEY_uid+","+AppInfo.KEY_appName+","
                +AppInfo.KEY_packageName+","+AppInfo.KEY_times+","+AppInfo.KEY_appOrder+" FROM "+AppInfo.TABLE
                +" order by "+AppInfo.KEY_appOrder+","+AppInfo.KEY_times+" desc ";
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public int getCount(SQLiteDatabase db){//查询数据总量
        String selectQuery = "SELECT count(*) from "+AppInfo.TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst())
            return cursor.getInt(0);
        return 0;
    }

    public Cursor onListPage(SQLiteDatabase db,int page,int num) {//分页查询数据
        String selectQuery = "SELECT "+AppInfo.KEY_uid+","+AppInfo.KEY_appName+","
                +AppInfo.KEY_packageName+","+AppInfo.KEY_times+","+AppInfo.KEY_appOrder+" FROM "+AppInfo.TABLE
                +" order by "+AppInfo.KEY_appOrder+","+AppInfo.KEY_times+" desc "+" limit "+page*num+","+num;
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public void onInsert(SQLiteDatabase db, AppInfo appInfo,int order) {//向表中添加。
        String insertQuery = "INSERT INTO "+AppInfo.TABLE+"("+AppInfo.KEY_uid+","+AppInfo.KEY_appName+","
                +AppInfo.KEY_packageName+","+AppInfo.KEY_times+","+AppInfo.KEY_appOrder+") VALUES (?,?,?,?,?)";
        db.execSQL(insertQuery,new String[]{appInfo.getUid()+"",appInfo.getAppName(),appInfo.getPackageName(),appInfo.getTimes()+"",order+""});
    }

    public void onDelete(SQLiteDatabase db, int[] id) {//从表删除。
        for (int i:id) {
            db.execSQL("delete from " + AppInfo.TABLE + " where "+ AppInfo.KEY_uid + " = "+ i);
        }
    }

    public void onClear(SQLiteDatabase db) {//从表删除。
        db.execSQL("delete from " + AppInfo.TABLE);
    }

    public void onUpdateTimes(SQLiteDatabase db, int id) {//更新次数
        db.execSQL("update "+AppInfo.TABLE+" set " + AppInfo.KEY_times+" = "+AppInfo.KEY_times + "+1 where "+ AppInfo.KEY_uid + " = "+ id);
    }

    public void onUpdateOrder(SQLiteDatabase db, int id,int order) {//更新顺序
        db.execSQL("update "+AppInfo.TABLE+" set " + AppInfo.KEY_appOrder+" = "+ order + " where "+ AppInfo.KEY_uid + " = "+ id);
    }

    public Cursor onQuery(SQLiteDatabase db, String appname) {//通过id查询
        Cursor cursor;
        String selectQuery =  "SELECT "+AppInfo.KEY_uid+","+AppInfo.KEY_appName+","
                +AppInfo.KEY_packageName+","+AppInfo.KEY_times+" FROM "+AppInfo.TABLE+" where "
                +AppInfo.KEY_appName+" like %"+appname+"%";
        cursor = db.rawQuery(selectQuery,null);
        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//更新数据库
        db.execSQL("DROP TABLE IF EXISTS " + AppInfo.TABLE);// Drop older table if existed, all data will be gone!!!
        onCreate(db);// Create tables again
    }
}