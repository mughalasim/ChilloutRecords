package com.chilloutrecords.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.chilloutrecords.R;
import com.chilloutrecords.activities.StartUpActivity;
import com.chilloutrecords.utils.ChilloutRecords;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM ";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {

            Log.e(TAG, "Message From: " + remoteMessage.getFrom());
            Log.e(TAG, "Message Data: " + remoteMessage.getData());
            Log.e(TAG, "Message Type: " + remoteMessage.getMessageType());
            Log.e(TAG, "Message Notification: " + remoteMessage.getNotification());

            Bundle data = new Bundle();
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                data.putString(entry.getKey(), entry.getValue());
            }

            Log.e(TAG, "PUSH Notification");
            String title = data.getString("title");
            String body = data.getString("body");
            sendNotification(title, body);

        }
    }

    private void sendNotification(String title, String body) {
        Intent intent = new Intent(this, StartUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("notification_title", title);
        intent.putExtra("notification_body", body);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, title)
                .setSmallIcon(R.drawable.ic_info)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(ContextCompat.getColor(ChilloutRecords.getAppContext(), R.color.colorPrimary), 1000, 1000)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }

        ShortcutBadger.applyCount(MessagingService.this, 1);

    }

}
