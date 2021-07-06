package com.example.smstozoho;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CancelReceiver extends BroadcastReceiver {

    private static CancelListener cancelListener;
    @Override
    public void onReceive(Context context, Intent intent) {
        String id = intent.getStringExtra("id");
        String msg = intent.getStringExtra("msg");
        String time = intent.getStringExtra("time");
        cancelListener.actionCancel(id,msg,time);
    }

    public static void bindListener(CancelListener listener){
        cancelListener = listener;

    }
}
