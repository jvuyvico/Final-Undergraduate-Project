package com.feifei.testv3;

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

    public void startScan() {
        if(!isScanning) {
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
        } else {
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

    private void addDevice(String device_name, String mac_address, String uuid, String major, String minor) {
        if(!current_scan_hashmap.containsValue(uuid)){
            current_scan_hashmap.put(mac_address, uuid);
            BLE_Device ble_device = new BLE_Device(device_name, mac_address, uuid, major, minor);
            ma.ble_arrayList.add(ble_device);
            ma.ble_listAdapter.notifyDataSetChanged();
            //ma.arrayList.add(mac_address);
            //ma.arrayAdapter.notifyDataSetChanged();

            //Check Logs to double check if may nahanap ba talaga
            Log.d(TAG, "Uy may nahanap ako");
            Log.d(TAG, "Device Name: " + "\"" + device_name +"\"");
            Log.d(TAG, "Mac Address: " + mac_address);
            Log.d(TAG, "UUID: " + uuid);
            Log.d(TAG, "Major: " + major);
            Log.d(TAG, "Minor: " + minor);
        }
    }

    // function to convert data bytes from beacon to hex array //
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

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            String hexScanRecord = bytesToHex(result.getScanRecord().getBytes());
            String iBeaconInfoString =
                    hexScanRecord.substring("02011A1AFF4C000215".length(), 58);
            String parsedUUID = iBeaconInfoString.substring(0, iBeaconInfoString.length() - 8);
            String parsedMinor = iBeaconInfoString.substring(parsedUUID.length() + 4, iBeaconInfoString.length() - 2);
            String parsedMajor = iBeaconInfoString.substring(parsedUUID.length(), parsedUUID.length() + 4);

            addDevice(result.getDevice().getName(), result.getDevice().getAddress(), parsedUUID, parsedMajor, parsedMinor);

            Log.d(TAG, "Scanned a device");
        }
    };
}
