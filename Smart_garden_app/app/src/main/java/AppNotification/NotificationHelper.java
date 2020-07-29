package AppNotification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import Helper.Constants;
import com.example.smartgarden.R;

import Helper.DeviceInformation;
import GardenManagement.DeviceManagement.DeviceDetailActivity;

public class NotificationHelper {

    public static void displayNotification(String title, String body, Context context){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_local_florist_black_24dp)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, mBuilder.build());
    }

    public static void displayDeviceNotification(DeviceInformation info, String title, String body, Context context){
        Intent showDeviceDetail = new Intent(context, DeviceDetailActivity.class);
        showDeviceDetail.putExtra("device_detail.device_id",
               info.getDevice_id());
        showDeviceDetail.putExtra("device_detail.device_name",
                info.getDevice_name());
        showDeviceDetail.putExtra("device_detail.device_type",
                info.getDevice_type());
        showDeviceDetail.putExtra("device_detail.linked_device_id",
                info.getLinked_device_id());
        showDeviceDetail.putExtra("device_detail.linked_device_name",
                info.getLinked_device_name());

        PendingIntent intent = PendingIntent.getActivity(context, 1, showDeviceDetail, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_local_florist_black_24dp)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, mBuilder.build());
    }
}
