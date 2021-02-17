package com.example.networkstatus;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    TextView wifiStatus;
    TextView bluetoothStatus;
    ToggleButton bluetoothButton;
    TextView mobileDataStatus;
    IntentFilter mIntent;

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, mIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiStatus = (TextView)findViewById(R.id.wifi_status);
        bluetoothStatus = (TextView)findViewById(R.id.bluetooth_status);
        bluetoothButton = (ToggleButton) findViewById(R.id.bluetoothButton);
        mobileDataStatus = (TextView) findViewById(R.id.mobile_data_status);

        mIntent = new IntentFilter("my_status");


        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(bluetoothAdapter != null){
                    if(bluetoothAdapter.isEnabled()){
                        bluetoothAdapter.disable();
                    } else {
                        bluetoothAdapter.enable();
                    }
                }
            }
        });
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        startService(intent);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                if(intent.getBooleanExtra("wifi_status", false)){
                    wifiStatus.setText("On");
                } else {
                    wifiStatus.setText("Off");
                }
                if(intent.getBooleanExtra("bluetooth_status", false)){
                    bluetoothStatus.setText("On");
                    bluetoothButton.setChecked(true);
                } else {
                    bluetoothStatus.setText("Off");
                    bluetoothButton.setChecked(false);
                }
                if (intent.getBooleanExtra("mobile_data_status", false)){
                    mobileDataStatus.setText("On");
                } else {
                    mobileDataStatus.setText("Off");
                }
        }
    };


}