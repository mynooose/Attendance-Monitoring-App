package com.example.firestorecheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiReceiver extends BroadcastReceiver {

    String TAG = "mkm";

    @Override
    public void onReceive(Context context, Intent intent) {

        //if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (info != null && info.isConnected()) {
            Log.d(TAG, "DocumentSnapshot successfully written!");
            // Do your work.
            Intent repeating_intent = new Intent(context.getApplicationContext(), TestActivity.class);
            repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(repeating_intent);
            // e.g. To check the Network Name or other info:
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
        }
    }
}