package com.feifei.testv4;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.feifei.testv4.ActivityPages.MainActivity;
import com.feifei.testv4.Classes.Attendance_Data;
import com.feifei.testv4.Classes.Scan_Data;
import com.feifei.testv4.Classes.User_Subject;
import com.feifei.testv4.SQLite.DatabaseAccess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PingAlarmService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static final String TAG = "PingAlarmService";
    public static boolean found;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = this;

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                //start process
                if( MainActivity.BT_Mode ) {
                    SimpleDateFormat simpleDateFormat;
                    int class_index = Utils.getCurrentSubjectIndex(context);
                    ArrayList<User_Subject> userSubjects_AL = Utils.getClassesToday(context);
                    User_Subject current_subject = userSubjects_AL.get(class_index);

                    int hour_start = current_subject.getTimestart() / 100;
                    int minute_start = current_subject.getTimestart() % 100;
                    int hour_end = current_subject.getTimeend() / 100;
                    int minute_end = current_subject.getTimeend() % 100;
                    int ping_interval = (((hour_end-hour_start)*60) + minute_end - minute_start - 10) * 60/9;   // ping interval in s

                    Calendar calendar_ping = Calendar.getInstance();
                    calendar_ping.set(Calendar.HOUR_OF_DAY, hour_start);
                    calendar_ping.set(Calendar.MINUTE, minute_start + 9*(ping_interval/60));
                    calendar_ping.set(Calendar.SECOND, 9*(ping_interval%60));

                    long time_difference = calendar_ping.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

                    AutoScanner autoScanner = new AutoScanner(context);
                    autoScanner.startScan();

                    found = false;
                    //wait for process to finish
                    int counter = 0;
                    while (!found && counter <= 6) {
                        Log.d(TAG, "run: Autoscanner still running. Counter = " + String.valueOf(counter));
                        counter += 1;
                        SystemClock.sleep(10*1000);
                    }

                    Log.d(TAG, "run: Sleep cycles taken: " + String.valueOf(counter));
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    ArrayList<Integer> pings_AL = databaseAccess.getPings();
                    databaseAccess.close();

                    if(time_difference < 30*1000 && time_difference > -30*1000){
                        while (pings_AL.size() < 10) {
                            databaseAccess.open();
                            databaseAccess.insertPing(pings_AL.size(), 0);
                            pings_AL.clear();
                            pings_AL.addAll(databaseAccess.getPings());
                            databaseAccess.close();
                            Log.d(TAG, "run: There was a ping that was not recorded. Adding extra entry.");
                        }

                        //log attendance
                        String status = "";
                        if ( pings_AL.stream().mapToInt(Integer::intValue).sum() > 6 ) {
                            status = "Present";
                        } else {
                            status = "Absent";
                        }
                        simpleDateFormat = new SimpleDateFormat("y-M-d E");
                        String date = simpleDateFormat.format(Calendar.getInstance().getTime());
                        simpleDateFormat = new SimpleDateFormat("h:m a");
                        String time = simpleDateFormat.format(Calendar.getInstance().getTime());

                        Scan_Data temp = new Scan_Data("Ping Alarm Check", time, String.valueOf(pings_AL.size()));
                        Attendance_Data dummydata = new Attendance_Data(current_subject.getSubject()+current_subject.getSection(), status, current_subject.getUuid(), current_subject.getMajor(), current_subject.getMinor(), date, time);
                        databaseAccess.open();
                        databaseAccess.insertScanData(temp);
                        databaseAccess.insertAttendanceData(dummydata);
                        databaseAccess.clearPings();
                        databaseAccess.close();

                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                        managerCompat.cancel(1);
                    }
                } else {
                    Utils.mode_Discoverable(context);
                    Log.d(TAG, "run: Start of Discoverability");
                }
            }
        }).start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "run: Autoscanner finished. Killing tread");
    }
}
