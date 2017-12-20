/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package asimmughal.chilloutrecords.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.facebook.notifications.NotificationsManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.start_up.SplashScreenActivity;
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

            if (NotificationsManager.canPresentCard(data)) {
                Log.e(TAG, "IN-APP Notification");
                NotificationsManager.presentNotification(
                        this,
                        data,
                        new Intent(getApplicationContext(), SplashScreenActivity.class)
                );

            } else {
                Log.e(TAG, "PUSH Notification");
                String title = data.getString("title");
                String body = data.getString("body");
                sendNotification(title, body);
            }
        }
    }

    private void sendNotification(String MessageTitle, String messageBody) {
        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("notification_title", MessageTitle);
        intent.putExtra("notification_body", messageBody);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MessageTitle)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(MessageTitle)
                .setContentText(messageBody)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(getResources().getColor(R.color.colorPrimary), 1000, 1000)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }

        ShortcutBadger.applyCount(MessagingService.this, 1);

    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ?
                R.mipmap.ic_launcher :
                R.mipmap.ic_launcher;
    }

}
