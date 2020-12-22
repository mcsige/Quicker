package com.smc.quicker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.smc.quicker.activity.MainActivity;
import com.smc.quicker.service.FloatingService;
import com.smc.quicker.util.DBHelper;

public class FloatBroadcastReceiver extends BroadcastReceiver {
    public static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    public static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";

    private View view;
    private Context context;

    public FloatBroadcastReceiver(View view,Context context) {

        this.view = view;
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            case VOLUME_CHANGED_ACTION:
                if(intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_SYSTEM)
                    view.invalidate();
                break;
            case Intent.ACTION_PACKAGE_REMOVED:
                Bundle b = intent.getExtras();
                int uid = b.getInt(Intent.EXTRA_UID);
                MainActivity.uninstall(uid);
                DBHelper dbHelper = new DBHelper(context, "appinfo.db", null, 3);//创建帮助器对象
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                dbHelper.onDelete(database,new int[]{uid});
                database.close();
            case Intent.ACTION_PACKAGE_ADDED:
                FloatingService.initPackageList();
                break;
        }
    }
}
