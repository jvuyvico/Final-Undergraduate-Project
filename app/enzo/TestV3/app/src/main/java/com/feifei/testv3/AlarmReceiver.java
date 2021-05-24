package com.feifei.testv3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new Thread(new Runnable(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                // dont care variables (overridable)
                SimpleDateFormat simpleDateFormat;
                Scan_Data scanData;
                Intent intenttopass;
                AlarmSetter alarmSetter;
                String time;
                Boolean dummyBool;

                // one-time instantiate variables
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                ArrayList<User_Subject> classesToday_AL = Utils.getClassesToday(context);

                // other variables;
                int hour_start;
                int minute_start;
                int hour_end;
                int minute_end;

                // open resources
                databaseAccess.open();

                // ! START MAIN CODE ! //

                /* For debugging purposes */
                Calendar calendar_now = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("E M/d/y h:m a");
                time = simpleDateFormat.format(calendar_now.getTime());
                scanData = new Scan_Data("Alarm reset check", time , "00");
                dummyBool = databaseAccess.insertScanData(scanData);
                /**/

                /* ---------------- Set individual alarms for each class today ------------------ */
                for(int i = 0; i < classesToday_AL.size(); i++) {
                    alarmSetter = new AlarmSetter(context, i);
                    intenttopass = new Intent(context, ClassAlarmReceiver.class);
                    alarmSetter.cancelAlarm(intenttopass);

                    User_Subject newUserSubject = classesToday_AL.get(i);

                    //set alarm for this subject
                    hour_start = newUserSubject.getTimestart() / 100;                               // calendar for first ping check time
                    minute_start = newUserSubject.getTimestart() % 100;
                    Calendar calendar_start = Calendar.getInstance();
                    calendar_start.set(Calendar.HOUR_OF_DAY, hour_start);
                    calendar_start.set(Calendar.MINUTE, minute_start);                              // first ping check after 15 mins of start of class
                    calendar_start.set(Calendar.SECOND, 0);

                    hour_end = newUserSubject.getTimeend() / 100;                                   // calendar for end time
                    minute_end = newUserSubject.getTimeend() % 100;
                    Calendar calendar_end = Calendar.getInstance();
                    calendar_end.set(Calendar.HOUR_OF_DAY, hour_end);
                    calendar_end.set(Calendar.MINUTE, minute_end);
                    calendar_end.set(Calendar.SECOND, 0);

                    long timedifference = calendar_end.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                    simpleDateFormat = new SimpleDateFormat("E M/d/y h:m a");
                    time = simpleDateFormat.format(calendar_start.getTime());

                    if (timedifference > 0) {
                        Log.d("Alarm: (Subject)", newUserSubject.getSubject() + " " + time + " : Alarm Set!");
                        alarmSetter.setAlarmManager(calendar_start, intenttopass);
                    } else {
                        Log.d("Alarm: (Subject)", newUserSubject.getSubject() + " " + time + " has passed. No alarm set.");
                    }

                }
                /* ------------------------------------------------------------------------------ */

                /* ------------------- Restart the alarm for the next day ----------------------- */
                alarmSetter = new AlarmSetter(context, 20);
                intenttopass = new Intent(context, AlarmReceiver.class);
                alarmSetter.cancelAlarm(intenttopass);
                calendar_now = Calendar.getInstance();

                calendar_now.set(Calendar.DAY_OF_MONTH, calendar_now.get(Calendar.DAY_OF_MONTH)+1);
                calendar_now.set(Calendar.HOUR_OF_DAY, 0);
                calendar_now.set(Calendar.MINUTE, 0);
                calendar_now.set(Calendar.SECOND, 0);

                simpleDateFormat = new SimpleDateFormat("E M/d/y h:m a");
                time = simpleDateFormat.format(calendar_now.getTime());
                Log.d("Alarm:", "Reset for " + time);

                alarmSetter.setAlarmManager(calendar_now, intenttopass);
                /* ------------------------------------------------------------------- */

                // terminate any resources accessed
                databaseAccess.close();

            }
        }).start();
    }
}
