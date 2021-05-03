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
                // initialize variables
                AutoScanner autoScanner;

                AlarmSetter alarmSetter;
                Calendar calendar;
                Intent intenttopass;
                SimpleDateFormat simpleDateFormat;
                String time;

                calendar = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("E M/d/y h:m a");
                time = simpleDateFormat.format(calendar.getTime());
                Scan_Data newScanData = new Scan_Data("Alarm reset check", time , "00");
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                boolean test = databaseAccess.insertScanData(newScanData);
                databaseAccess.close();

                /*
                if( MainActivity.BT_Mode ) {
                    autoScanner = new AutoScanner(context);
                    autoScanner.startScan();
                } else {
                    Utils.mode_Discoverable(context);
                }
                 */


                /* ---------------- Set individual alarms for each class today ------------------ */
                ArrayList<User_Subject> classesToday_AL = Utils.getClassesToday(context);

                for(int i = 0; i < classesToday_AL.size(); i++) {
                    alarmSetter = new AlarmSetter(context, i);
                    intenttopass = new Intent(context, ClassAlarmReceiver.class);
                    alarmSetter.cancelAlarm(intenttopass);

                    int ihour = 0;
                    int iminute = 0;

                    User_Subject newUserSubject = classesToday_AL.get(i);

                    //set alarm for this subject
                    ihour = newUserSubject.getTimeend() / 100;
                    iminute = newUserSubject.getTimeend() % 100;

                    Calendar icalendar = Calendar.getInstance();
                    icalendar.set(Calendar.HOUR_OF_DAY, ihour);
                    icalendar.set(Calendar.MINUTE, iminute);
                    icalendar.set(Calendar.SECOND, 0);

                    long timedifference = icalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                    simpleDateFormat = new SimpleDateFormat("E M/d/y h:m a");
                    time = simpleDateFormat.format(icalendar.getTime());

                    Log.d("Alarm: (Subject): ", classesToday_AL.get(i).getSubject()+ " " + String.valueOf(ihour) + ":" + String.valueOf(iminute));


                    if (timedifference > 0) {
                        Log.d("Alarm: (Subject): ", newUserSubject.getSubject() + " " + time + " : Alarm Set!");
                        alarmSetter.setAlarmManager(icalendar, intenttopass);
                    } else {
                        Log.d("Alarm: (Subject): ", newUserSubject.getSubject() + " " + time + " has passed. No alarm set.");
                    }

                }
                /* ------------------------------------------------------------------------------ */


                /* ------------------- Restart the alarm for the next day ----------------------- */
                alarmSetter = new AlarmSetter(context, 20);
                intenttopass = new Intent(context, AlarmReceiver.class);
                alarmSetter.cancelAlarm(intenttopass);
                calendar = Calendar.getInstance();

                // Check if this rolls over at the end of the month
                /*
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+1);
                calendar.set(Calendar.SECOND, 0);
                */
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                simpleDateFormat = new SimpleDateFormat("E M/d/y h:m a");
                time = simpleDateFormat.format(calendar.getTime());
                Log.d("Alarm: (Reset): ", time);

                alarmSetter.setAlarmManager(calendar, intenttopass);

                Log.d("Alarm: ", "Reset");
                /* ------------------------------------------------------------------- */
            }
        }).start();
    }
}
