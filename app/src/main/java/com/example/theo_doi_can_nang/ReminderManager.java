package com.example.theo_doi_can_nang;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import java.util.Calendar;

public class ReminderManager {

    private static final int REQUEST_CODE = 2001;

    public static void setReminder(
            Context context,
            int hour,
            int minute) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE
                );

        if (alarmManager == null) {
            return;
        }

        Intent intent =
                new Intent(
                        context,
                        WeightReminderReceiver.class
                );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        REQUEST_CODE,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        Calendar calendar =
                Calendar.getInstance();

        calendar.set(
                Calendar.HOUR_OF_DAY,
                hour
        );

        calendar.set(
                Calendar.MINUTE,
                minute
        );

        calendar.set(
                Calendar.SECOND,
                0
        );

        calendar.set(
                Calendar.MILLISECOND,
                0
        );

        // Nếu giờ hôm nay đã qua thì đặt sang ngày mai
        if (calendar.getTimeInMillis()
                <= System.currentTimeMillis()) {

            calendar.add(
                    Calendar.DAY_OF_YEAR,
                    1
            );
        }

        long triggerTime =
                calendar.getTimeInMillis();

        /*
         * Android 12 trở lên:
         * Nếu được phép dùng exact alarm thì
         * sử dụng setExactAndAllowWhileIdle().
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (alarmManager.canScheduleExactAlarms()) {

                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );

            } else {

                // Chưa có quyền exact alarm
                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );
            }

        } else {

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        }
    }

    public static void cancelReminder(
            Context context) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE
                );

        Intent intent =
                new Intent(
                        context,
                        WeightReminderReceiver.class
                );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        REQUEST_CODE,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        pendingIntent.cancel();
    }

    public static boolean canScheduleExactAlarm(
            Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(
                            Context.ALARM_SERVICE
                    );

            return alarmManager != null &&
                    alarmManager.canScheduleExactAlarms();
        }

        return true;
    }
}