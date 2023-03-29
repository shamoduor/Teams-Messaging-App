package com.shamine.teamsmessagingapp.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.utils.Utils;

import java.util.Map;

public class FirebaseMessageReceiver extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Map<String, String> dataMap = message.getData();

        String picUrl = dataMap.containsKey("picUrl") ? dataMap.get("picUrl") : null;
        String title = dataMap.containsKey("title") ? dataMap.get("title") : "-";
        String messageContent = dataMap.containsKey("message") ? dataMap.get("message") : "-";
        String createdOn = dataMap.containsKey("createdOn") ? dataMap.get("createdOn") : "0";
        String sender = dataMap.containsKey("sender") ? dataMap.get("sender") : "0";
        String receiver = dataMap.containsKey("receiver") ? dataMap.get("receiver") : "0";

        if (!Utils.stringNullOrEmpty(messageContent)) {
            messageContent = Utils.decrypt(messageContent, createdOn, sender, receiver);

            /*
               String msg = Utils.decrypt(m.getContent(), String.valueOf(m.getCreatedOn()),
                    String.valueOf(m.getSenderId()), String.valueOf(m.getChatGroupId()));
             */
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(getResources().getString(R.string.default_notification_channel_id), "General", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Default notification channel");
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int pendingFlags = Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_ONE_SHOT;

        PendingIntent pendingIntent  = PendingIntent.getActivity(this, 0, intent, pendingFlags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getResources().getString(R.string.default_notification_channel_id));
        builder.setContentTitle(title);
        builder.setContentText(messageContent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(messageContent));
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.temp_user);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);

        notificationManager.notify(0, builder.build());
    }
}
