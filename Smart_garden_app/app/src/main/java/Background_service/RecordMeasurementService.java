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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import AppNotification.NotificationHelper;
import Database.Garden_Database_Control;
import DeviceController.Device_Control;
import Helper.DeviceInformation;
import Helper.VolleyCallBack;
import Login_RegisterUser.UserLoginManagement;

public class RecordMeasurementService extends Service{
    public int counter=0;
    private DeviceInformation[] device_list;
    private Vector<DeviceInformation> sensors;
    private Vector<Integer> sensors_position;
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
                    sensors = UserLoginManagement.getInstance(getApplicationContext()).getSensor();
                    sensors_position = new Vector<>();
                    for(int i = 0; i< sensors.size(); i++){
                            sensors_position.addElement(i);
                    }
                    Garden_Database_Control.recordMeasurement_v2(sensors, sensors_position, getApplicationContext());
                }
            }
        };
        timer.schedule(timerTask, 1000, 12000);
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

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//    public void onSuccessResponse(String response) {
//        int index = response.indexOf("<br");
//        String change_response = response.substring(0, index);
//        if (change_response.equals(""))
//            return;
//        String[] responses = change_response.split("\n");
//        for (String res : responses) {
//            Log.i("Message", res);
//            try {
//                JSONObject jsonObject = new JSONObject(res);
//                String message = jsonObject.getString("message");
//                if (message.equals("Turn on")) {
//                    int position = jsonObject.getInt("position");
//                    Device_Control.turnDeviceOn(sensors.get(position).getLinked_device_id(),
//                            sensors.get(position).getLinked_device_name(), getApplicationContext());
//
//                    // Send notification
//                    NotificationHelper.displayDeviceNotification(sensors.get(position) ,"Warning",
//                            "Device" + sensors.get(position).getDevice_id() + " is sending a warning",
//                            getApplicationContext());
//                } else if (message.equals("Turn off")) {
//                    int position = jsonObject.getInt("position");
//                    Device_Control.turnDeviceOff(sensors.get(position).getLinked_device_id(),sensors.get(position).getLinked_device_name(), getApplicationContext());
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
