package com.feifei.testv4;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.util.Log;

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

    private static final String TAG = "AutoScanner";

    private int scan_interval_ms = 60*1000;
    private boolean isScanning = false;

    public AutoScanner(Context context) {
        this.scanTimer = new Timer();
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        this.autoScan_Hashmap = new HashMap<>();
    }

    // start BLE scan
    public void startScan() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {                             // don't scan if bluetooth is off. App will crash
            if(!isScanning) {                                                                       // * if app is not yet in a scanning mode, start scan
                scanTimer.schedule(new TimerTask() {                                                // delay function to stop scanning after a set time
                    @Override
                    public void run() {
                        isScanning = false;
                        bluetoothLeScanner.stopScan(leScanCallback);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E M/d/y h:m a");
                        String time = simpleDateFormat.format(Calendar.getInstance().getTime());

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
                Log.d(TAG, "Scan started");
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

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E M/d/y h:m a");
            String time = simpleDateFormat.format(calendar.getTime());
            Scan_Data newScanData = new Scan_Data(uuid, time , rssi);

            com.feifei.testv4.DatabaseAccess databaseAccess = com.feifei.testv4.DatabaseAccess.getInstance(context);
            databaseAccess.open();
            boolean test = databaseAccess.insertScanData(newScanData);
            databaseAccess.close();
            Log.d(TAG, "Insert data successful - " + String.valueOf(test));
        }
    }

    // receiving and parsing of information from beacon
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            //parse the received bytes
            String hexScanRecord = com.feifei.testv4.Utils.bytesToHex(result.getScanRecord().getBytes());
            String iBeaconInfoString =
                    hexScanRecord.substring("02011A1AFF4C000215".length(), hexScanRecord.length());
            String parsedUUID = iBeaconInfoString.substring(0, 32);
            String parsedMajor = iBeaconInfoString.substring(32, 36);
            String parsedMinor = iBeaconInfoString.substring(36, 40);

            //convert Hex to Decimal for student number viewing
            parsedMajor = String.valueOf( Integer.parseInt(parsedMajor ,16) );
            parsedMinor = String.valueOf( Integer.parseInt(parsedMinor ,16) );


            com.feifei.testv4.DatabaseAccess databaseAccess = com.feifei.testv4.DatabaseAccess.getInstance(context);
            databaseAccess.open();
            ArrayList<User_Subject> userSubjects_AL = databaseAccess.getData();
            databaseAccess.close();
            ArrayList<String> uuid_AL = new ArrayList<>();

            //for efficiency purposes, make an array that removes redundant UUIDs for the cross check process
            for ( int i = 0; i < userSubjects_AL.size(); i++) {
                if ( !uuid_AL.contains(userSubjects_AL.get(i).getUuid()) ){
                    uuid_AL.add(userSubjects_AL.get(i).getUuid());
                }
            }

            // add scan data if detected UUID is crosschecked from database
            if( uuid_AL.contains(parsedUUID) ){
                addDevice(result.getDevice().getName(), result.getDevice().getAddress(), parsedUUID, parsedMajor, parsedMinor, String.valueOf(result.getRssi()));
            }

            Log.d(TAG, "Scanned a device");
        }
    };

}
