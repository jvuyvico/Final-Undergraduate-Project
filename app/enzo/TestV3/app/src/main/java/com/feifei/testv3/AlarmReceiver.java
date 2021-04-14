package com.feifei.testv3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                /*
                for (int i = 0; i<15; i++){
                    Log.d("Alarm: ", "run " +i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                 */
                User_Subject userSubject = new User_Subject("EEE2", "MX", "M", 900, 1000);
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                boolean test = databaseAccess.insertData(userSubject);
                databaseAccess.close();
                Log.d("Test", String.valueOf(test));
                Log.d("Alarm: ", "Job Finished");
                AlarmSetter alarmSetter = new AlarmSetter(context);
                alarmSetter.cancelAlarm();
                Calendar calendar = Calendar.getInstance();
                //add error checking if its the last day of the month
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+1);
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, 0);
                alarmSetter.setAlarmManager(calendar);
                /*

                 */
            }
        }).start();
    }


}
