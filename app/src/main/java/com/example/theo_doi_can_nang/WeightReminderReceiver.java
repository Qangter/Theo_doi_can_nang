package com.example.theo_doi_can_nang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class WeightReminderReceiver
        extends BroadcastReceiver {

    @Override
    public void onReceive(
            Context context,
            Intent intent) {

        // Tạo notification channel
        NotificationHelper.createNotificationChannel(
                context
        );

        // Hiển thị notification
        NotificationHelper.showNotification(
                context
        );

        // Đọc cài đặt reminder
        SharedPreferences prefs =
                context.getSharedPreferences(
                        "app_settings",
                        Context.MODE_PRIVATE
                );

        boolean reminderEnabled =
                prefs.getBoolean(
                        "reminder_enabled",
                        false
                );

        // Nếu người dùng vẫn bật reminder
        // thì đăng ký cho ngày tiếp theo
        if (reminderEnabled) {

            int hour =
                    prefs.getInt(
                            "reminder_hour",
                            7
                    );

            int minute =
                    prefs.getInt(
                            "reminder_minute",
                            0
                    );

            ReminderManager.setReminder(
                    context,
                    hour,
                    minute
            );
        }
    }
}