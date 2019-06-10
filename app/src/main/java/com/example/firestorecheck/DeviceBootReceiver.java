package com.example.firestorecheck;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 30);
            //calendar.set(Calendar.SECOND, 59);

            intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Calendar.getInstance().after(calendar) || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                calendar.add(Calendar.DAY_OF_YEAR, 1);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//other notification
            calendar.set(Calendar.HOUR_OF_DAY, 18);
            calendar.set(Calendar.MINUTE, 00);
            //calendar.set(Calendar.SECOND, 59);

            intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent_out = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Calendar.getInstance().after(calendar) || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                calendar.add(Calendar.DAY_OF_YEAR, 1);

            AlarmManager alarmManager_out = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager_out.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent_out);


        }

    }
}
