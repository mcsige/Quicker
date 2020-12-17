package com.smc.quicker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.View;

public class VolumeBroadcastReceiver extends BroadcastReceiver {
    public static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    public static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";

    private View view;

    public VolumeBroadcastReceiver(View view) {
        this.view = view;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction()==VOLUME_CHANGED_ACTION
                && intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE,-1)==AudioManager.STREAM_SYSTEM){
            view.invalidate();
        }
    }
}
