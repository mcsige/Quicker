package com.smc.quicker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.View;

public class FloatBroadcastReceiver extends BroadcastReceiver {
    public static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    public static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
        public static final String PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";

    private View view;

    public FloatBroadcastReceiver(View view) {
        this.view = view;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case VOLUME_CHANGED_ACTION:
                if(intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_SYSTEM)
                    view.invalidate();
                break;
            case PACKAGE_REMOVED:
                //接收不到
                Log.e("smc","卸载");
                break;
        }
    }
}
