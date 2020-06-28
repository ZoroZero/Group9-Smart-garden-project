package AppNotification;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

public class AppFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification()!= null){
            String title = remoteMessage.getNotification().getTitle();
            String context = remoteMessage.getNotification().getBody();
            NotificationHelper.displayNotification(title, context, getApplicationContext());
//            Log.d("AppNotification", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
