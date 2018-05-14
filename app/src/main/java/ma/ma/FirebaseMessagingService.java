package ma.ma;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Nitharani on 25/04/2018.
 */
//https://developer.android.com/training/notify-user/build-notification
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

       // String notif_title = remoteMessage.getNotification().getTitle();
       // String notif_msg = remoteMessage.getNotification().getBody();
        //getting the action from the index.js file
       String cAction = remoteMessage.getNotification().getClickAction();
       //receving the user id and storing it in a string
       String fromUid = remoteMessage.getData().get("from_userid");
    //create a builder for notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
               //sets the icon as the logo
                .setSmallIcon(R.mipmap.ic_launcher)
                //sets title for the notification
                .setContentTitle("New Request")
                //sets the message for the notification
                .setContentText("Cocoon received a request")
                //how invasive a notification should be
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //when the notification is clicked, it will start the intent
        // and redirect to the profile.java
        Intent rIntent = new Intent(cAction);
        //providing the user id to the user who sent the notification
        rIntent.putExtra("id", fromUid);

        //builds a pending intent and allow us to start the intent
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        rIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        //set the intent to the builder
        mBuilder.setContentIntent(resultPendingIntent);



        //sets id for the notification - provides unique ID each time
        int mNotificationId = (int) System.currentTimeMillis();
        //retrieves the instance of notificationmanager
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //builds the notification
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
