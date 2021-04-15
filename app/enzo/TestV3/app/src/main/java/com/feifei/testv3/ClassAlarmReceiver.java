package com.feifei.testv3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class ClassAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                //scan and add to database

            }
        }).start();
    }

}
