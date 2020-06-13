package Background_service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import Database.Garden_Database_Control;
import Helper.DeviceInformation;
import Login_RegisterUser.UserLoginManagement;

public class RecordMeasurementService extends Service {
    public int counter=0;
    private DeviceInformation[] device_list;
    //private MqttAndroidClient client = MQTTHelper.client;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();

//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction("RestartService");
//        broadcastIntent.setClass(this, RestartService.class);
//        this.sendBroadcast(broadcastIntent);
    }

    private Timer timer;

    public void startTimer() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void run() {
                if (isOnline()) {
                    Log.i("Count", "=========  " + (counter++));
                    if (!UserLoginManagement.getInstance(getApplicationContext()).isLoggedIn()) {
                        stoptimertask();
                    }
                    device_list = UserLoginManagement.getInstance(getApplicationContext()).getDevice_list();
                    if(device_list == null){
                        return;
                    }
                    for (DeviceInformation device : device_list) {
                        if (device.getDevice_type().contains("Sensor"))
                            Garden_Database_Control.recordMeasurement(device.getDevice_name() + "/" + device.getDevice_id(),
                                    device.getDevice_type().replace(" Sensor", ""), device.getDevice_id(), getApplicationContext());
                        //Log.i("Topic", jsonObject.getString("device_id") + "/" + jsonObject.getString("device_name"));
                    }
                }
            }
        };
        timer.schedule(timerTask, 1000, 300000);
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }
    }

}
