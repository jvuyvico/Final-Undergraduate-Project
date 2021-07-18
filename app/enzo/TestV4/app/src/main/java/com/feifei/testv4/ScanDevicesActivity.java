package com.feifei.testv4;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.feifei.testv4.Classes.BLE_Device;

import java.util.ArrayList;

public class ScanDevicesActivity extends AppCompatActivity {

    Button button_scanDevices;
    ListView lv_scanDevices;
    ArrayList<BLE_Device> ble_arrayList;
    BLE_ListAdapter ble_listAdapter;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Scanner ble_scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_devices);

        button_scanDevices = (Button) findViewById(R.id.device_scan);
        lv_scanDevices = (ListView) findViewById(R.id.lv_device_scan);
        ble_scanner = new Scanner(this);

        ble_arrayList = new ArrayList<>();
        ble_listAdapter = new BLE_ListAdapter(this, R.layout.ble_device_list_item, ble_arrayList);
        lv_scanDevices.setAdapter(ble_listAdapter);

        // add dummy device to check if list and listadapter is working
        BLE_Device dummydevice = new BLE_Device("DummyDevice", "46:BE:ED:BD:44:E5", "602EB8EB20EC04872040B4A52740CE18", "20xxX", "XXXX", "-50");
        ble_arrayList.add(dummydevice);
        ble_listAdapter.notifyDataSetChanged();
    }


    public void scanDevices ( View view ) {
        Utils.checkBluetooth(bluetoothAdapter, this);
        ble_scanner.startScan();
    }

}