package com.example.myapplication.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

/**
 * Helper class to handle notifications in the app.
 */
public class NotificationHelper {

    private static final String CHANNEL_ID = "calorie_notification_channel";
    private static final String CHANNEL_NAME = "Calorie Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for daily calorie updates";

    /**
     * Sends a notification to the user.
     *
     * @param context The context from which the notification is sent.
     * @param title   The title of the notification.
     * @param message The content text of the notification.
     */
    public static void sendNotification(Context context, String title, String message) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel if the device runs Android 8.0 or higher.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESCRIPTION);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Build the notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app's icon if available.
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true); // Dismiss the notification when tapped.

        // Show the notification.
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}
