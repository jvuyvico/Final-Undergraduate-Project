package com.example.testv2;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISABLE_BT = 0;
    private TextView mDeviceList;

    private static final String LOG_TAG = "MainActivity";
    private static final String TAGline = "MainActivity";
    private static final String TAGline2 = "MainActivity";

    private Handler scanHandler = new Handler();

    ArrayList arrayList;
    ListView mlistofDevices;
    ArrayAdapter<String> arrayAdapter;
    Button BTScan;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

    private int scan_interval_ms = 15000;
    private boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BTScan = (Button) findViewById(R.id.BT_Scan);


        //Part ito for parsing and para mapakita sa listview dun sa activity_main.xml
        mlistofDevices = (ListView) findViewById(R.id.DeviceList);
        arrayList = new ArrayList();
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,arrayList);
        mlistofDevices.setAdapter(arrayAdapter);

        //Used in normal Bluetooth
        //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //registerReceiver(receiver, filter);

        //Permission checks lang. Important though na i-approve mo yung permission request for location, so double check mo doon sa settings itself.
        int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
        }


    }

    public void BT_Switch(View view) { //function linked to switch on or of button.
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        }
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
            Toast toast = Toast.makeText(this, R.string.toast_message,
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }



    public void BTScan(View view) {

        if(!isScanning) {
            scanHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                    Log.d(TAGline2, "Went to stop scan");
                    BTScan.setText("Scan BLE");

                }
            }, scan_interval_ms);

            isScanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
            Log.d(TAGline, "Went to start scan");
            BTScan.setText("Scanning...");
        } else {
            isScanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
            Log.d(TAGline2, "Went to stop scan");
            BTScan.setText("Scan BLE");
        }

        /*
        if (bluetoothLeScanner != null) {
            if (isScanning) {
                Log.d(TAGline2, "Went to stop scan");
                bluetoothLeScanner.stopScan(leScanCallback);

            } else {
                Log.d(TAGline, "Went to start scan");
                bluetoothLeScanner.startScan(leScanCallback);


            }
        }
        isScanning = !isScanning;
        scanHandler.postDelayed(this::stopScan, scan_interval_ms);

         */
    }


    private void stopScan() {
        bluetoothLeScanner.stopScan(leScanCallback);
        leScanCallback = null;
        scanHandler = null;

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
            // To get UUID from IBeacon, we need to parse the data bytes it sends
            String hexScanRecord = bytesToHex(result.getScanRecord().getBytes());
            String iBeaconInfoString = hexScanRecord.substring(18, 58);
            String parsedUUID = iBeaconInfoString.substring(0, iBeaconInfoString.length() - 8);
            String parsedMinor = iBeaconInfoString.substring(parsedUUID.length() + 4, iBeaconInfoString.length() - 2);
            String parsedMajor = iBeaconInfoString.substring(parsedUUID.length(), parsedUUID.length() + 4);

            //Parsing Part na di ko magets bat ayaw gumagana. Nagccrash siya sa akin when parsing
            arrayList.add(result.getDevice().getName());
            arrayAdapter.notifyDataSetChanged();
            //Check Logs to double check if may nahanap ba talaga
            Log.d(TAGline, "Uy may nahanap ako");
            Log.d(LOG_TAG, "DeviceName: " + "\"" + result.getDevice().getName() +"\"");
            Log.d(LOG_TAG, "UUID: " + parsedUUID);
            //Check getDevice class para malaman ano pang pwede maextract na information
        }
    };

/* This part is mostly for Normal Bluetooth Scanning
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arrayList.add(device.getName());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

 */
}