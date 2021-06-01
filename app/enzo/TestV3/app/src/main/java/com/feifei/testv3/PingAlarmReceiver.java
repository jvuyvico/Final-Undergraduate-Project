package com.feifei.testv3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class PingAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                context.startService(new Intent(context, PingAlarmService.class));

                Calendar calendar_now = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E M/d/y h:m:s a");
                String time = simpleDateFormat.format(calendar_now.getTime());

                /*
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                ArrayList<Integer> pings_AL = databaseAccess.getPings();
                databaseAccess.close();

                //for debugging purposes
                Log.d("Ping Alarm", "Ping started at " + time + ". Prior number of pings is " + String.valueOf(pings_AL.size()) + " with values " + pings_AL.toString());
                 */

            }
        }).start();
    }
}
