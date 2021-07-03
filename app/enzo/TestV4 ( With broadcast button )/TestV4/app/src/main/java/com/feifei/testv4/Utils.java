
package com.feifei.testv4;

/*
    Put utility functions here and call them to reduce clutter in other activities
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;

import com.feifei.testv4.ActivityPages.MainActivity;
import com.feifei.testv4.Classes.User_Subject;
import com.feifei.testv4.SQLite.DatabaseAccess;

import java.nio.ByteBuffer;
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

    public static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

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

    public static int getCurrentSubjectIndex (Context context) {                                    // index from list of classes for the day only
        int subject_index = -1;

        Calendar calendar_now = Calendar.getInstance();
        int now_ms = calendar_now.get(Calendar.HOUR_OF_DAY)*60 +calendar_now.get(Calendar.MINUTE);
        ArrayList<User_Subject> classesToday_AL = getClassesToday(context);

        for(int i = 0; i < classesToday_AL.size(); i++) {
            User_Subject newUserSubject = classesToday_AL.get(i);

            int hour_start = newUserSubject.getTimestart() / 100;
            int minute_start = newUserSubject.getTimestart() % 100;
            int start_ms = hour_start*60 + minute_start;

            int hour_end = newUserSubject.getTimeend() / 100;
            int minute_end = newUserSubject.getTimeend() % 100;
            int end_ms = hour_end*60 + minute_end;

            if( (end_ms > now_ms) && (now_ms >= start_ms) ) {
                subject_index = i;
            }
        }
        return subject_index;
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

    public static void mode_Discoverable( Context context ) {
        BluetoothLeAdvertiser bleadvertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        //permission check if device can advertise ble packets
        if( !BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported() ) {
            //Toast.makeText( context, "Multiple advertisement not supported", Toast.LENGTH_SHORT ).show();
            Log.e("BLE", "Multiple ads not supported");
        }
        if(BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported() ) {
            //Toast.makeText(context, "Multiple advertisement supported", Toast.LENGTH_SHORT).show();
            Log.e("BLE", "Multiple ads supported");


            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                    .setTimeout(180*1000)
                    .build();

            ParcelUuid self_uuid = new ParcelUuid(UUID.fromString(context.getString(R.string.ble_user_uuid)));
            UUID selfbleuuid = UUID.fromString("2e952a2b-eef3-4a80-a309-6a3f5aacb1e8");
            //UUID set for advertising is 2e952a2b-eef3-4a80-a309-6a3f5aacb1e8

            //This process is for creating payload as iBeacon
            byte[] selfuuidbytes = Utils.asBytes(selfbleuuid);
            byte[] payload_1 = {(byte)0x02, (byte)0x15, (byte)0x00}; // this makes it an iBeacon
            byte[] payload_3 = {
                    (byte)0x20, (byte)0x15,  // Set Major
                    (byte)0x18, (byte)0x27}; // Set Minor

            byte[] payload = new byte[payload_1.length + selfuuidbytes.length + payload_3.length];
            System.arraycopy(payload_1, 0, payload, 0, payload_1.length);
            System.arraycopy(selfuuidbytes, 0, payload, payload_1.length, selfuuidbytes.length);
            System.arraycopy(payload_3, 0, payload, payload_1.length + selfuuidbytes.length, payload_3.length);

            AdvertiseData data = new AdvertiseData.Builder()
                    .setIncludeDeviceName(false)
                    .addManufacturerData(0x004C, payload)
                    .build();
/*
            AdvertiseData advScanResponse = new AdvertiseData.Builder()
                    .setIncludeDeviceName(true)
                    .build();
*/
            String user_uuid = context.getString(R.string.ble_user_uuid);


            AdvertiseCallback adCallback = new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                    Log.e(" BLE", "On start discovery success");
                    //Toast.makeText(context, "UUID is " + user_uuid + "\n Advertising set for 5 secs", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStartFailure(int errorCode) {
                    Log.e("BLE", "Advertising onStartFailure: " + errorCode);
                    super.onStartFailure(errorCode);
                }
            };
            bleadvertiser.startAdvertising(settings, data,  adCallback);
        }
    }

}
