package com.example.theo_doi_can_nang;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    public static final String CHANNEL_ID =
            "weight_reminder_channel";

    public static final int NOTIFICATION_ID =
            1001;


    // =========================================================
    // Tạo Notification Channel
    // =========================================================

    public static void createNotificationChannel(
            Context context) {

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O) {

            String channelName =
                    "Nhắc cân nặng";

            String channelDescription =
                    "Thông báo nhắc người dùng ghi nhận cân nặng";

            int importance =
                    NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            channelName,
                            importance
                    );

            channel.setDescription(
                    channelDescription
            );

            NotificationManager manager =
                    context.getSystemService(
                            NotificationManager.class
                    );

            if (manager != null) {

                manager.createNotificationChannel(
                        channel
                );
            }
        }
    }


    // =========================================================
    // Hiển thị Notification
    // =========================================================

    public static void showNotification(
            Context context) {

        NotificationManager manager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                android.R.drawable.ic_dialog_info
                        )
                        .setContentTitle(
                                "Theo dõi cân nặng"
                        )
                        .setContentText(
                                "Đã đến giờ ghi nhận cân nặng!"
                        )
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(
                                                "Đã đến giờ ghi nhận cân nặng! " +
                                                        "Hãy mở ứng dụng và cập nhật cân nặng hôm nay."
                                        )
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_DEFAULT
                        )
                        .setAutoCancel(true);

        if (manager != null) {

            manager.notify(
                    NOTIFICATION_ID,
                    builder.build()
            );
        }
    }
}

