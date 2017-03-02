package com.example.hiennguyen.firebaseexample.service.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.hiennguyen.firebaseexample.detail.MainActivity;
import com.example.hiennguyen.firebaseexample.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseNotificationService extends FirebaseMessagingService {

    private static final String TAG = FirebaseNotificationService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Message containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        Intent intent = new Intent(this, MainActivity.class);

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage, intent);
        }
    }

    /**
     * Display notification messages.
     */
    private void showNotification(RemoteMessage remoteMessage, Intent intent) {

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        android.support.v7.app.NotificationCompat.Builder builder = new android.support.v7.app.NotificationCompat
                .Builder(this);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setSmallIcon(getNotificationIcon());
        builder.setAutoCancel(true);
        notificationManager.notify("type", 0, builder.build());
    }

    /**
     * Get notification icon.
     */
    private int getNotificationIcon() {
        return 0;
    }
}
