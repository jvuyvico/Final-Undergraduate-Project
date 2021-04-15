package com.feifei.testv3;

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

import java.util.HashMap;

public class Scanner {

    private MainActivity ma;

    private Handler scanHandler;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private HashMap current_scan_hashmap;

    private static final String TAG = "Scanner";

    private int scan_interval_ms = 20000;
    private boolean isScanning = false;


    public Scanner(MainActivity mainActivity) {
        ma = mainActivity;
        this.scanHandler = new Handler();
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        this.current_scan_hashmap = new HashMap<>();

    }

    // start BLE scan
    public void startScan() {
        if(!isScanning) { // if app is not yet in a scanning mode, start scan

            // delay function to stop scanning after a set time
            scanHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                    Log.d(TAG, "Went to stop scan");
                    ma.BTScan.setText("Scan BLE");
                    Toast.makeText(ma.getApplicationContext(), "Scan Complete", Toast.LENGTH_SHORT).show();

                }
            }, scan_interval_ms);

            ma.ble_arrayList.clear();
            ma.ble_listAdapter.notifyDataSetChanged();
            current_scan_hashmap.clear();
            isScanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
            Log.d(TAG, "Went to start scan");
            ma.BTScan.setText("Stop Scan");
            Toast.makeText(ma.getApplicationContext(), "Scan Starting", Toast.LENGTH_SHORT).show();

        } else { // else if app is in a scanning mode, stop current scan
            isScanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
            Log.d(TAG, "Went to stop scan");
            ma.BTScan.setText("Scan BLE");
            Toast.makeText(ma.getApplicationContext(), "Scan Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopScan() {
        bluetoothLeScanner.stopScan(leScanCallback);
        leScanCallback = null;
        scanHandler = null;
    }

    // pass the parsed information in a BLE_Device class and send it to the ArrayList in MainActivity
    private void addDevice(String device_name, String mac_address, String uuid, String major, String minor, String rssi) {
        if(!current_scan_hashmap.containsValue(uuid)){

            // 602EB8EB20EC04872040B4A52740CE18 - add UUID filter to added devices

            current_scan_hashmap.put(mac_address, uuid);
            BLE_Device ble_device = new BLE_Device(device_name, mac_address, uuid, major, minor, rssi);
            ma.ble_arrayList.add(ble_device);
            ma.ble_listAdapter.notifyDataSetChanged();

            //Check Logs to double check if may nahanap ba talaga
            Log.d(TAG, "Uy may nahanap ako");
            Log.d(TAG, "Device Name: " + "\"" + device_name +"\"");
            Log.d(TAG, "Mac Address: " + mac_address);
            Log.d(TAG, "UUID: " + uuid);
            Log.d(TAG, "Major: " + major);
            Log.d(TAG, "Minor: " + minor);
            Log.d(TAG, "RSSI: " + rssi);
        }
    }

    // function to convert data bytes from beacon to hex array
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    // receiving and parsing of information from beacon
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            String hexScanRecord = bytesToHex(result.getScanRecord().getBytes());
            String iBeaconInfoString =
                    hexScanRecord.substring("02011A1AFF4C000215".length(), hexScanRecord.length());
            String parsedUUID = iBeaconInfoString.substring(0, 32);
            String parsedMajor = iBeaconInfoString.substring(32, 36);
            String parsedMinor = iBeaconInfoString.substring(36, 40);

            parsedMajor = String.valueOf( Integer.parseInt(parsedMajor ,16) );
            parsedMinor = String.valueOf( Integer.parseInt(parsedMinor ,16) );

            addDevice(result.getDevice().getName(), result.getDevice().getAddress(), parsedUUID, parsedMajor, parsedMinor, String.valueOf(result.getRssi()));

            Log.d(TAG, "Scanned a device " + iBeaconInfoString.length());
        }
    };
}
