package com.example.networkstatus;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyService extends Service {

    String wifiStatus, bluetoothStatus, mobileDataStatus;
    NotificationCompat.Builder mBuilder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(runnable);
        startMyService();
        Log.i("new", "new2");
        return START_STICKY;
    }

    private void startMyService(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel();
        } else {
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle("");
            builder.setStyle(bigTextStyle);
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setPriority(Notification.PRIORITY_MAX);
            builder.setFullScreenIntent(pendingIntent, true);
            Notification notification = builder.build();
            startForeground(1, notification);
        }
    }



    private void createNotificationChannel(){
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationChannel channel = new NotificationChannel("my_channel_id", "my_channel_name", NotificationManager.IMPORTANCE_HIGH);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "my_channel_id");
        mBuilder = notificationBuilder;
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("")
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOnlyAlertOnce(true)
                .setContentIntent(resultPendingIntent) //intent
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, notificationBuilder.build());
        startForeground(1, notification);
    }


    Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try{
                sendMyBroadcast();
                updateStatus();
            }finally {
                handler.postDelayed(runnable, 1000);
            }
        }
    };


    void sendMyBroadcast(){
        Intent intent = new Intent("my_status");
        intent.putExtra("wifi_status", isWifiEnabled(getApplicationContext()));
        intent.putExtra("bluetooth_status", isBluetoothEnabled());
        intent.putExtra("mobile_data_status", isMobileDataEnabled(getApplicationContext()));
        sendBroadcast(intent);
    }


    public void updateStatus(){
        if(isWifiEnabled(getApplicationContext())){
            wifiStatus = "On";
        } else {
            wifiStatus = "Off";
        }
        if(isBluetoothEnabled()){
            bluetoothStatus = "On";
        } else {
            bluetoothStatus = "Off";
        }
        if(isMobileDataEnabled(getApplicationContext())){
            mobileDataStatus = "On";
        } else {
            mobileDataStatus = "Off";
        }
        mBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setContentTitle("Wifi: " + wifiStatus + ", Bluetooth: " + bluetoothStatus + ", Mobile Data: " + mobileDataStatus);
        NotificationManager manager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, mBuilder.build());
    }

    public boolean isWifiEnabled(Context context){
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(wifi.isWifiEnabled()){
            return true;
        } else return false;
    }

    public boolean isBluetoothEnabled(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    public boolean isMobileDataEnabled(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities activeNetwork = cm.getNetworkCapabilities(cm.getActiveNetwork());
        if (activeNetwork != null) {
            if (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true;
            } else return false;
        }
        return false;
    }


}
