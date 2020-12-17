package com.smc.quicker.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class SharedPreferencesHelper {
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    @SuppressLint("CommitPrefEdits")
    public SharedPreferencesHelper(Context activity){
        sharedPreferences = activity.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(sharedPreferences.getInt("row",0)==0){
            editor.putInt("row",3);
            editor.putInt("col",4);
            editor.putBoolean("autoSave",false);
            editor.commit();
        }
    }

    public int[] getRowCol(){
        int[] rowCol = new int[2];
        rowCol[0] = sharedPreferences.getInt("row",0);
        rowCol[1] = sharedPreferences.getInt("col",0);
        return rowCol;
    }

    public void setRowCol(int[] rowCol){
        editor.putInt("row",rowCol[0]);
        editor.putInt("col",rowCol[1]);
        editor.commit();
    }

    public boolean getAutoSave(){
        boolean autoSave;
        autoSave = sharedPreferences.getBoolean("autoSave",false);
        return autoSave;
    }

    public void setAutoSave(boolean autoSave){
        editor.putBoolean("autoSave",autoSave);
        editor.commit();
    }
}
