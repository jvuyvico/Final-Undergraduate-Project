package com.feifei.testv3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ClassAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        new Thread(new Runnable(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                // dont care variables (overridable)
                SimpleDateFormat simpleDateFormat;
                String time;
                String date;
                Scan_Data scanData;
                Boolean dummyBool;

                // one-time instantiate variables
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                int subject_index = Utils.getCurrentSubjectIndex(context);
                ArrayList<User_Subject> classesToday_AL = Utils.getClassesToday(context);
                User_Subject current_subject = classesToday_AL.get(subject_index);

                // other variables;
                int ping_interval;
                int hour_start;
                int minute_start;
                int hour_end;
                int minute_end;
                long time_difference;

                // open resources
                databaseAccess.open();

                // ! START MAIN CODE ! //

                /* For debugging purposes */
                Calendar calendar_now = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("E M/d/y h:m a");
                time = simpleDateFormat.format(calendar_now.getTime());
                scanData = new Scan_Data("Class Alarm Check", time , "-45");
                dummyBool = databaseAccess.insertScanData(scanData);
                Log.d("Class Alarm", "Triggered");
                /**/

                hour_start = current_subject.getTimestart() / 100;
                minute_start = current_subject.getTimestart() % 100;
                hour_end = current_subject.getTimeend() / 100;
                minute_end = current_subject.getTimeend() % 100;
                ping_interval = (((hour_end-hour_start)*60) + minute_end - minute_start - 10) * 60/9;   // ping interval in s

                Log.d("Class Alarm", "This alarm is for " + classesToday_AL.get(subject_index).getSubject() +
                        ". Pinging at an interval of " + String.valueOf(ping_interval) + "s");

                for(int i = 0; i < 10; i++) {                                                       // set alarm for each ping
                    AlarmSetter alarmSetter = new AlarmSetter(context, i+50);           // requestcode values 50-59 are for pings
                    Intent intenttopass = new Intent(context, PingAlarmReceiver.class);

                    Calendar calendar_ping = Calendar.getInstance();
                    calendar_ping.set(Calendar.HOUR_OF_DAY, hour_start);
                    calendar_ping.set(Calendar.MINUTE, minute_start + i*(ping_interval/60));
                    calendar_ping.set(Calendar.SECOND, i*(ping_interval%60));

                    simpleDateFormat = new SimpleDateFormat("y-M-d E");
                    date = simpleDateFormat.format(calendar_ping.getTime());
                    simpleDateFormat = new SimpleDateFormat("h:m:s a");
                    time = simpleDateFormat.format(calendar_ping.getTime());

                    time_difference = calendar_ping.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

                    if (time_difference >= 0) {
                        Log.d("Class Alarm", "Set Ping Alarm for " + time);
                        alarmSetter.setAlarmManager(calendar_ping, intenttopass);
                    } else {
                        Log.d("Class Alarm", "Late for ping at " + time);
                        ArrayList<Integer> pings = new ArrayList<>();
                        pings.addAll(databaseAccess.getPings());

                        if (i == pings.size()){
                            databaseAccess.insertPing(i, 0);
                        }
                        if (i == 9) {
                            User_Subject dd = classesToday_AL.get(subject_index);
                            Attendance_Data newData = new Attendance_Data(dd.getSubject(), "Absent", dd.getUuid(), "20150", "4617", date, time);
                            dummyBool = databaseAccess.insertAttendanceData(newData);
                        }
                    }
                }

                // terminate any resources accessed
                databaseAccess.close();

            }
        }).start();
    }
}
