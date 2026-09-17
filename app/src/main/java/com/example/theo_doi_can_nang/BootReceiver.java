package com.example.theo_doi_can_nang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            android.content.SharedPreferences prefs =
                    context.getSharedPreferences(
                            "app_settings",
                            Context.MODE_PRIVATE
                    );

            boolean reminderEnabled =
                    prefs.getBoolean(
                            "reminder_enabled",
                            false
                    );

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
}