package com.feifei.testv4;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.feifei.testv4.Classes.Scan_Data;
import com.feifei.testv4.Classes.User_Subject;
import com.feifei.testv4.SQLite.DatabaseAccess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AutoScanner {

    private Timer scanTimer;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private HashMap autoScan_Hashmap;
    private Context context;
    private int class_index;
    private ArrayList<User_Subject> userSubjects_AL;
    private int ping_number;
    private boolean beacon_found;
    private DatabaseAccess databaseAccess;

    private static final String TAG = "AutoScanner";

    private int scan_interval_ms = 45*1000;
    private boolean isScanning = false;

    public AutoScanner(Context context) {
        this.scanTimer = new Timer();
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        this.autoScan_Hashmap = new HashMap<>();
        this.class_index = Utils.getCurrentSubjectIndex(context);
        this.userSubjects_AL = Utils.getClassesToday(context);

        this.beacon_found = false;


        this.databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
    }

    // start BLE scan
    public void startScan() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {                             // don't scan if bluetooth is off. App will crash
            if(!isScanning) {                                                                       // * if app is not yet in a scanning mode, start scan
                scanTimer.schedule(new TimerTask() {                                                // delay function to stop scanning after a set time
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        isScanning = false;
                        bluetoothLeScanner.stopScan(leScanCallback);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:m a");
                        String time = simpleDateFormat.format(Calendar.getInstance().getTime());

                        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                        databaseAccess.open();
                        ArrayList<Integer> pings_AL = databaseAccess.getPings();
                        databaseAccess.insertPing(pings_AL.size(), beacon_found ? 1 : 0);
                        databaseAccess.close();

                        Log.d(TAG, "Went to stop scan : " + time);
                    }
                }, scan_interval_ms);

                ScanFilter.Builder filter_builder = new ScanFilter.Builder();                       //used scan filter to allow scanning while screen is locked
                ScanFilter filter = filter_builder.build();
                List<ScanFilter> scanFilterList = new ArrayList<>();
                scanFilterList.add(filter);

                ScanSettings settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)                               //to be explored, minsan di madetect kung ibang setting like LOW_POWER
                        .build();

                autoScan_Hashmap.clear();
                isScanning = true;


                bluetoothLeScanner.startScan(scanFilterList, settings, leScanCallback);
                Log.d(TAG, "Scan started for class " + userSubjects_AL.get(class_index).getSubject() +
                        ". Scanning for beacon with UUID: " + userSubjects_AL.get(class_index).getUuid());
            } else {                                                                                // * else if app is in a scanning mode, stop current scan
                isScanning = false;
                bluetoothLeScanner.stopScan(leScanCallback);
                Log.d(TAG, "Scan stopped");
            }
        } else {
            Log.d(TAG, "Scan failed: Bluetooth state");
        }
    }

    //insert new scan data to database
    private void addDevice(String device_name, String mac_address, String uuid, String major, String minor, String rssi) {
        if(!autoScan_Hashmap.containsValue(mac_address)){
            autoScan_Hashmap.put(uuid, mac_address);
            //BLE_Device ble_device = new BLE_Device(device_name, mac_address, uuid, major, minor, rssi);
            beacon_found = true;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E M/d/y h:m a");
            String time = simpleDateFormat.format(Calendar.getInstance().getTime());

            Scan_Data newScanData = new Scan_Data(uuid, time , rssi);
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
            boolean test = databaseAccess.insertScanData(newScanData);

            Log.d(TAG, "Beacon found");
        }
    }

    // receiving and parsing of information from beacon
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            //parse the received bytes
            String hexScanRecord = Utils.bytesToHex(result.getScanRecord().getBytes());
            String iBeaconInfoString =
                    hexScanRecord.substring("02011A1AFF4C000215".length(), hexScanRecord.length());
            String parsedUUID = iBeaconInfoString.substring(0, 32);
            String parsedMajor = iBeaconInfoString.substring(32, 36);
            String parsedMinor = iBeaconInfoString.substring(36, 40);

            //convert Hex to Decimal for student number viewing
            parsedMajor = String.valueOf( Integer.parseInt(parsedMajor ,16) );
            parsedMinor = String.valueOf( Integer.parseInt(parsedMinor ,16) );

            User_Subject currentSubject = userSubjects_AL.get(class_index);
            // add scan data if detected UUID is crosschecked from database
            if( currentSubject.getUuid().contains(parsedUUID) ){
                if ( currentSubject.getMajor().contains(parsedMajor) && currentSubject.getMinor().contains(parsedMinor) ) {

                }
                addDevice(result.getDevice().getName(), result.getDevice().getAddress(), parsedUUID, parsedMajor, parsedMinor, String.valueOf(result.getRssi()));
            } else {
                Log.d(TAG, "Detected a random device");
            }
        }
    };

}
