package com.feifei.testv4;

/*
    Third Stage Alarm. Calls a service since its work takes a long time
       (i.e. not enough time before it gets killed in the background)
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
            }
        }).start();
    }
}
