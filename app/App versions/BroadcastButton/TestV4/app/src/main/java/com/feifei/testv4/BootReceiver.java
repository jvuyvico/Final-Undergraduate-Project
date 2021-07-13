package com.feifei.testv4;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Boot completed", Toast.LENGTH_LONG).show();

        boolean alarmUp = (PendingIntent.getBroadcast(context, 20, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp){
            Log.d("Alarm: ", "Alarm is already active");
        } else {
            Intent intenttopass = new Intent(context, AlarmReceiver.class);
            AlarmSetter alarmSetter = new AlarmSetter(context, 20);
            Calendar calendar = Calendar.getInstance();
            alarmSetter.setAlarmManager(calendar, intenttopass);
            Log.d("Alarm: ", "Alarm set");
        }
    }
}
