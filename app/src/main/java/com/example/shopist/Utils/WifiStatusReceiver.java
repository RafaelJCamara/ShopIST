package com.example.shopist.Utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class WifiStatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) {
            Toast.makeText(context, activeNetworkInfo.getTypeName() + " network connected", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(context, "No Internet or Network connection available", Toast.LENGTH_LONG).show();
        }
    }
}
