package com.feifei.testv4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ClassAlarmReceiver extends BroadcastReceiver {

    private AutoScanner autoScanner;

    @Override
    public void onReceive(Context context, Intent intent) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                //scan and add to database
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E M/d/y h:m a");
                String time = simpleDateFormat.format(calendar.getTime());
                Scan_Data newScanData = new Scan_Data("Class Alarm Check", time , "-45");

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                boolean test = databaseAccess.insertScanData(newScanData);
                databaseAccess.close();
                Log.d("Class Alarm: ", "Triggered");

                if( MainActivity.BT_Mode ) {
                    autoScanner = new AutoScanner(context);
                    autoScanner.startScan();
                } else {
                    Utils.mode_Discoverable(context);
                }


            }
        }).start();
    }

}
