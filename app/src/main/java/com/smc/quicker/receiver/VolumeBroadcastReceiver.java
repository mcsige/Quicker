package com.smc.quicker.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.rtp.AudioStream;
import android.util.Log;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class VolumeBroadcastReceiver extends BroadcastReceiver {
    public static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    public static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";

    private Context root;

    public VolumeBroadcastReceiver(Context root) {
        this.root = root;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (VOLUME_CHANGED_ACTION.equals(intent.getAction())
                && intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1)==AudioManager.STREAM_SYSTEM){
            ActivityManager activityManager = (ActivityManager) root.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(2);//参数是想获得的个数，可以随意写
            //获取到最上面的进程
            if(tasks.size()>1) {
                ActivityManager.RunningTaskInfo taskInfo = tasks.get(1);
                //获取到最顶端应用程序的包名
                String packageName = taskInfo.topActivity.getPackageName();
                Log.e("smc",packageName);
            }
        }
    }
}
