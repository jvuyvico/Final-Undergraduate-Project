package com.feifei.testv3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        new Thread(new Runnable(){
            @Override
            public void run() {
                // initialize variables
                AlarmSetter alarmSetter;
                Calendar calendar;
                Intent intenttopass;


                User_Subject userSubject = new User_Subject("EEE2", "MX", "M", 900, 1000, "602EB8EB20EC04872040B4A52740CE18");
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                boolean test = databaseAccess.insertData(userSubject);
                databaseAccess.close();
                Log.d("Test", String.valueOf(test));
                Log.d("Alarm: ", "Job Finished");



                /* ---------------- Set individual alarms for each class today ------------------ */
                ArrayList<User_Subject> classesToday_AL = Utils.getClassesToday(context);

                for(int i = 0; i < classesToday_AL.size(); i++) {
                    alarmSetter = new AlarmSetter(context, i);
                    intenttopass = new Intent(context, AlarmReceiver.class);
                    alarmSetter.cancelAlarm(intenttopass);

                    calendar = Calendar.getInstance();
                    int ihour = 0;
                    int iminute = 0;

                    User_Subject newUserSubject = classesToday_AL.get(i);

                    //set alarm for this subject
                    ihour = newUserSubject.getTimestart() / 100;
                    iminute = newUserSubject.getTimestart() % 100;

                    Calendar icalendar = Calendar.getInstance();
                    icalendar.set(Calendar.HOUR_OF_DAY, ihour);
                    icalendar.set(Calendar.MINUTE, iminute);
                    icalendar.set(Calendar.SECOND, 0);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
                    String time = simpleDateFormat.format(icalendar.getTime());

                    Log.d("Alarm: (Subject): ", newUserSubject.getSubject() + " " + time);
                    //alarmSetter.setAlarmManager(icalendar, intenttopass);
                }



                /* ------------------------------------------------------------------------------ */




                /* ------------------- Restart the alarm for the next day ----------------------- */
                alarmSetter = new AlarmSetter(context, 20);
                intenttopass = new Intent(context, AlarmReceiver.class);
                alarmSetter.cancelAlarm(intenttopass);
                calendar = Calendar.getInstance();

                /* ADD IF FINISHED DEBUGGING
                if (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)){
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                } else {
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+1);
                }
                 */

                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)+1);
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, 0);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
                String time = simpleDateFormat.format(calendar.getTime());
                Log.d("Alarm: (Reset): ", time);

                alarmSetter.setAlarmManager(calendar, intenttopass);
                /* ------------------------------------------------------------------- */
            }
        }).start();
    }
}
