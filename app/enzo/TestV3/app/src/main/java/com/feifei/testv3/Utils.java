package com.feifei.testv3;

/*
    Put utility functions here and call them to reduce clutter in other activities
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class Utils {
    public static void checkBluetooth(BluetoothAdapter bluetoothAdapter, Activity activity) {
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            requestUserBluetooth(activity);
        }
    }

    // Check if bluetooth is off. If off, request to turn on.
    public static void requestUserBluetooth(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT);
    }
/*
    public static void toast(Context context, String string) {

        Toast toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
        toast.show();
    }
 */
    public static UUID asUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    public static byte[] asBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public static ArrayList<User_Subject> getClassesToday(Context context){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        ArrayList<User_Subject> userSubjectArrayList = databaseAccess.getData();
        databaseAccess.close();

        Calendar calendar = Calendar.getInstance();
        int dayofweek = calendar.get(calendar.DAY_OF_WEEK);
        String stringdayofweek = "U"; // dummy default value

        switch (dayofweek) {
            case 1:
                stringdayofweek = "U";
                break;
            case 2:
                stringdayofweek = "M";
                break;
            case 3:
                stringdayofweek = "T";
                break;
            case 4:
                stringdayofweek = "W";
                break;
            case 5:
                stringdayofweek = "H";
                break;
            case 6:
                stringdayofweek = "F";
                break;
            case 7:
                stringdayofweek = "S";
                break;
        }

        ArrayList<User_Subject> newUserSubject_AL = new ArrayList<>();

        for(int i = 0; i < userSubjectArrayList.size(); i++) {  // loop through subjects to check if they are scheduled for today
            User_Subject newUserSubject = userSubjectArrayList.get(i);
            boolean todayistheday = newUserSubject.getDays().contains(stringdayofweek);
            if (todayistheday){
                //store subjects for today in an arraylist
                newUserSubject_AL.add(newUserSubject);
            } else {
            }
        }
        return newUserSubject_AL;
    }

    public static void setSubjectAlarm(Context context){
        //int timedifference = (int) (calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
        //String.valueOf(Collections.frequency(stringArrayList, "M")
        //simpleDateFormat = new SimpleDateFormat("h:mm a");
        //String time = simpleDateFormat.format(calendar.getTime());

        /*
        int ihour = 0;
        int iminute = 0;

        User_Subject newUserSubject;

        //set alarm for this subject
        ihour = newUserSubject.getTimestart() / 100;
        iminute = newUserSubject.getTimestart() % 100;

        Calendar icalendar = Calendar.getInstance();
        icalendar.set(Calendar.HOUR_OF_DAY, ihour);
        icalendar.set(Calendar.MINUTE, iminute);
        icalendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, icalendar.getTimeInMillis(), pendingIntent);
         */
        //Log.d("Alarm: ", "Yes " + String.valueOf(ihour) + String.valueOf(iminute));
    }

}
