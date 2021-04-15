package com.feifei.testv3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmSetter {

    private Context context;
    private int requestcode;

    public AlarmSetter(Context context, int requestcode) {
        this.context = context;
        this.requestcode = requestcode;
    }

    public void setAlarmManager(Calendar calendar, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestcode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public void cancelAlarm(Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestcode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
