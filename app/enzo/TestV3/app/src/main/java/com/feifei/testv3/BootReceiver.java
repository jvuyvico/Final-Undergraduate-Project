package com.feifei.testv3;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Boot completed", Toast.LENGTH_LONG).show();

        boolean alarmUp = (PendingIntent.getBroadcast(context, 20, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp){
            Log.d("Alarm: ", "Alarm is already active");
        } else {
            AlarmSetter alarmSetter = new AlarmSetter(context );
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+2); // +x to account for device boot up time
            calendar.set(Calendar.SECOND, 0);
            alarmSetter.setAlarmManager(calendar);
            Log.d("Alarm: ", "Alarm set");
        }
    }
}
