package com.example.smstozoho;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

import static com.example.smstozoho.MyService.itroter;

public class MmsReceiver extends BroadcastReceiver {

    private static ActionListener actionListener;
    @Override
    public void onReceive(Context context, Intent intent) {

        String notifid = intent.getStringExtra("id");
        String msgrec = intent.getStringExtra("msg");
        String time = intent.getStringExtra("time");
        intent.getAction();

            actionListener.actionReceived(notifid,msgrec,time);
    }

    public static void bindListener(ActionListener listener){
        actionListener = listener;

    }
}
