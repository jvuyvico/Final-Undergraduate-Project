package com.feifei.testv3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = this;

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                //start process
                if( MainActivity.BT_Mode ) {
                    MainActivity.killthread = false;

                    SimpleDateFormat simpleDateFormat;
                    int class_index = Utils.getCurrentSubjectIndex(context);
                    ArrayList<User_Subject> userSubjects_AL = Utils.getClassesToday(context);

                    AutoScanner autoScanner = new AutoScanner(context);
                    autoScanner.startScan();

                    //wait for process to finish
                    while(!MainActivity.killthread){
                        Log.d(TAG, "run: Autoscanner still running");
                        SystemClock.sleep(10*1000);
                    }


                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    ArrayList<Integer> pings_AL = databaseAccess.getPings();
                    databaseAccess.close();

                    if(pings_AL.size() == 10){
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

                        User_Subject dd = userSubjects_AL.get(class_index);
                        Attendance_Data dummydata = new Attendance_Data(dd.getSubject(), status, dd.getUuid(), "20150", "4617", date, time);
                        databaseAccess.open();
                        databaseAccess.insertAttendanceData(dummydata);
                        databaseAccess.clearPings();
                        databaseAccess.close();
                    }

                } else {
                    Utils.mode_Discoverable(context);;
                }
            }
        }).start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "run: Autoscanner finished. Killing tread");
    }
}
