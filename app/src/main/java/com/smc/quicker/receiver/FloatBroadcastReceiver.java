package com.smc.quicker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.View;

import com.smc.quicker.service.FloatingService;

public class FloatBroadcastReceiver extends BroadcastReceiver {
    public static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    public static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";

    private View view;

    public FloatBroadcastReceiver(View view) {
        this.view = view;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("smc",intent.getAction());
        switch (intent.getAction()) {
            case VOLUME_CHANGED_ACTION:
                if(intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_SYSTEM)
                    view.invalidate();
                break;
            case Intent.ACTION_PACKAGE_ADDED:
                Log.e("smc","安装");
            case Intent.ACTION_PACKAGE_REMOVED:
                Log.e("smc","卸载");
                FloatingService.initPackageList();
                break;
        }
    }
}
