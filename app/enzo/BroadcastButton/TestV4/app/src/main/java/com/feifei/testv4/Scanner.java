package com.feifei.testv4;

/*
    BLE scanner functionalities placed here from MainActivity. Just send MainActivity
        as an Activity parameter.
    Data bytes received from the broadcaster is parsed here.
    Found devices are passed towards ListView in MainActivity for display.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.feifei.testv4.Classes.BLE_Device;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Scanner {

    private ScanDevicesActivity activity;

    private Timer scanTimer;
    private Handler scanHandler;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private HashMap current_scan_hashmap;

    private static final String TAG = "Scanner";

    private int scan_interval_ms = 60000;
    private boolean isScanning = false;


    public Scanner(ScanDevicesActivity activity) {
        this.scanTimer = new Timer();
        this.activity = activity;
        this.scanHandler = new Handler();
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        this.current_scan_hashmap = new HashMap<>();

    }

    // start BLE scan
    public void startScan() {
        if(!isScanning) { // if app is not yet in a scanning mode, start scan

            // delay function to stop scanning after a set time
            /*
            scanHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                    Log.d(TAG, "Went to stop scan");
                    activity.button_scanDevices.setText("Scan BLE");
                    Toast.makeText(activity.getApplicationContext(), "Scan Complete", Toast.LENGTH_SHORT).show();

                }
            }, scan_interval_ms);
             */

            scanTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isScanning = false;
                    stopScan();
                    Log.d(TAG, "Went to stop scan");
                }
            }, scan_interval_ms);


            activity.ble_arrayList.clear();
            activity.ble_listAdapter.notifyDataSetChanged();
            current_scan_hashmap.clear();
            isScanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
            Log.d(TAG, "Went to start scan");
            //activity.button_scanDevices.setText("Stop Scan");
            Toast.makeText(activity.getApplicationContext(), "Scan Starting", Toast.LENGTH_SHORT).show();

        } else { // else if app is in a scanning mode, stop current scan
            isScanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
            Log.d(TAG, "Went to stop scan");
            //activity.button_scanDevices.setText("Scan BLE");
            Toast.makeText(activity.getApplicationContext(), "Scan Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopScan() {
        bluetoothLeScanner.stopScan(leScanCallback);
        //leScanCallback = null;
        //scanHandler = null;
    }

    // pass the parsed information in a BLE_Device class and send it to the ArrayList in MainActivity
    private void addDevice(String device_name, String mac_address, String uuid, String major, String minor, String rssi) {
        Log.d(TAG, "Uy may nahanap ako");
        Log.d(TAG, "Device Name: " + "\"" + device_name +"\"");
        Log.d(TAG, "Mac Address: " + mac_address);
        Log.d(TAG, "UUID: " + uuid);
        Log.d(TAG, "Major: " + major);
        Log.d(TAG, "Minor: " + minor);
        Log.d(TAG, "RSSI: " + rssi);

        if(!current_scan_hashmap.containsValue(mac_address)){
            current_scan_hashmap.put(uuid, mac_address);
            BLE_Device ble_device = new BLE_Device(device_name, mac_address, uuid, major, minor, rssi);
            activity.ble_arrayList.add(ble_device);
            activity.ble_listAdapter.notifyDataSetChanged();
        }
    }

    // function to convert data bytes from beacon to hex array

    // receiving and parsing of information from beacon
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            String hexScanRecord = Utils.bytesToHex(result.getScanRecord().getBytes());
            String iBeaconInfoString =
                    hexScanRecord.substring("02011A1AFF4C000215".length(), hexScanRecord.length());
            String parsedUUID = iBeaconInfoString.substring(0, 32);
            String parsedMajor = iBeaconInfoString.substring(32, 36);
            String parsedMinor = iBeaconInfoString.substring(36, 40);

            //convert Hex to Decimal for student number viewing
            parsedMajor = String.valueOf( Integer.parseInt(parsedMajor ,16) );
            parsedMinor = String.valueOf( Integer.parseInt(parsedMinor ,16) );

            addDevice(result.getDevice().getName(), result.getDevice().getAddress(), parsedUUID, parsedMajor, parsedMinor, String.valueOf(result.getRssi()));
            Log.d(TAG, "Scanned a device");

        }
    };
}
