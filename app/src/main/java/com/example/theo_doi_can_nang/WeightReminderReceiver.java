package com.example.theo_doi_can_nang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WeightReminderReceiver
        extends BroadcastReceiver {

    @Override
    public void onReceive(
            Context context,
            Intent intent) {

        // Tạo Notification Channel
        NotificationHelper.createNotificationChannel(
                context
        );

        // Hiển thị thông báo
        NotificationHelper.showNotification(
                context
        );
    }
}
