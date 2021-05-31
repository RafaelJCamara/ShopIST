package com.example.shopist.Utils.OfflineManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.shopist.Activities.MainActivityNav;

public class WifiStatusReceiver extends BroadcastReceiver {

    private boolean changesToPushToServer;
    public WifiStatusReceiver(){changesToPushToServer=false;}

    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) {
            Toast.makeText(context, activeNetworkInfo.getTypeName() + " network connected", Toast.LENGTH_SHORT).show();
            if(changesToPushToServer){
                //means there were changes offline made that need to be updated at the server

            }
            MainActivityNav.withWifi=true;
        } else {
            Toast.makeText(context, "No Internet or Network connection available", Toast.LENGTH_LONG).show();
            //from now on changes will be made offline
            MainActivityNav.withWifi=false;
        }
    }

    public void setChangesToPushToServer(boolean newValue){
        changesToPushToServer=newValue;
    }
}
