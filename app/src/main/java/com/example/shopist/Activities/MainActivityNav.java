package com.example.shopist.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cloudinary.android.MediaManager;
import com.example.shopist.R;
import com.example.shopist.Utils.CacheManager.SmallDataCacheManager;
import com.example.shopist.Utils.WaitTimeManager.SimWifiP2pBroadcastReceiver;
import com.example.shopist.Utils.OfflineManager.WifiStatusReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;

public class MainActivityNav extends AppCompatActivity {

    //beacon related attributes
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private boolean mBound = false;
    private SimWifiP2pBroadcastReceiver mReceiver;
    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mManager = new SimWifiP2pManager(new Messenger(service));
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };
    private WifiStatusReceiver wifiStatusReceiver;
    public static String currentUserId;
    public static boolean withWifi = true;
    public static SmallDataCacheManager smallDataCacheManager;

    //for letting the user know, in the pantry list context, which are the stores he can buy stuff on
//    public static ArrayList<String> currentExistingShoppingLists = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_pantries, R.id.navigation_shopping)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        smallDataCacheManager = new SmallDataCacheManager();

        initCloudSettings();
        fillTextView();
        addLogoutButtonLogic();
        initWifiDirectSettings();
        initWifiSettings();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void initCloudSettings(){
        Map config = new HashMap();
        config.put("cloud_name", "dy5jqy5fw");
        config.put("api_key", "941312846299731");
        config.put("api_secret", "1TjF4L4PRUT4K0r7bsTCWQYX12Q");
        MediaManager.init(getApplicationContext(), config);
    }

    private void initWifiDirectSettings(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);
        turnOnWifi();
    }

    private void turnOnWifi(){
        Intent intent = new Intent(MainActivityNav.this, SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;
    }

    private void initWifiSettings(){
        wifiStatusReceiver = new WifiStatusReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(wifiStatusReceiver, intentFilter);
    }


    private void fillTextView(){
        TextView textView = findViewById(R.id.textView);
        String info = "Welcome, ";
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null){
            if(extras.containsKey("username")){
                info+=intent.getStringExtra("username");
            }
        }else{
            info+="anonymous";
        }
        textView.setText(info);
        currentUserId = intent.getStringExtra("userId");
    }

    private void addLogoutButtonLogic(){
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityNav.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivityNav.this, "Logout with success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}