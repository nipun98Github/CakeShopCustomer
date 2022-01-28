package com.customer.esweetcustomer.Model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.customer.esweetcustomer.HomeActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FbMessagingService extends FirebaseMessagingService {
    private String TAG = "FbMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        super.onMessageReceived(remoteMessage);
        Log.d("msg", "onMessageReceived: " + remoteMessage.getData().get("message"));
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, channelId)

                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
//        // TODO(developer): Handle FCM messages here.
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, remoteMessage.getNotification().getTitle()+
//                    " Message Notification Body: " + remoteMessage.getNotification().getBody());
//            try {
//                Toast.makeText(this.getApplicationContext(), "New Message :" + remoteMessage.getNotification().getTitle() + "" + remoteMessage.getNotification().getBody(), Toast.LENGTH_SHORT).show();
//            }catch (Exception e){
//
//            }
//        }
    }
}
