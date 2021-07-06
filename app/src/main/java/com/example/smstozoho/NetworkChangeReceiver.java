package com.example.smstozoho;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                WifiManager.WIFI_STATE_UNKNOWN);

        switch (wifiStateExtra) {
            case WifiManager.WIFI_STATE_ENABLED:

                break;
            case WifiManager.WIFI_STATE_DISABLED:

                break;
        }

    }
}
