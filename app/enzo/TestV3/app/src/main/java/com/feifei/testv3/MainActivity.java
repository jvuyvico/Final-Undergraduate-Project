package com.feifei.testv3;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    /* Variable Declarations */
    public static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISABLE_BT = 0;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    BluetoothLeAdvertiser bleadvertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

    private static final String TAG = "MainActivity";
    private Scanner ble_scanner;

    // interface interactables
    Button BTScan;
    ImageView popupmenu;

    //for user credentials
    SharedPreferences sharedPreferences;
    public String user_Username;
    public String user_Studentnumber;
    TextView tv_Username;
    TextView tv_Studentnumber;

    //for scanned BLE devices list
    ListView ble_listView;
    ArrayList<BLE_Device> ble_arrayList;
    BLE_ListAdapter ble_listAdapter;

    /* ---------------------- */

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BTScan = (Button) findViewById(R.id.BT_Scan);
        tv_Username = findViewById(R.id.userCred_username);
        tv_Studentnumber = findViewById(R.id.userCred_studentnumber);
        ble_scanner = new Scanner(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);

        //Permission checks lang. Important though na i-approve mo yung permission request for location, so double check mo doon sa settings itself.
        int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
        }

        // Check if there is existing credentials in the CredentialsDB database
        if (!sharedPreferences.contains("username")) {
            Toast.makeText(this, "Please Set-up Credentials", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, AdminLoginActivity.class));
        }

        // Device list initialization
        ble_listView = (ListView) findViewById(R.id.DeviceList);
        ble_arrayList = new ArrayList<>();
        ble_listAdapter = new BLE_ListAdapter(this, R.layout.ble_device_list_item, ble_arrayList);
        ble_listView.setAdapter(ble_listAdapter);

        // add dummy device to check if list and listadapter is working
        BLE_Device dummydevice = new BLE_Device("DummyDevice", "46:BE:ED:BD:44:E5", "47805e3089b34efab715ae6ebd79ac9a", "0000", "00");
        ble_arrayList.add(dummydevice);
        ble_listAdapter.notifyDataSetChanged();

        // Check if bluetooth is turned on. If not, request.
        Utils.checkBluetooth(bluetoothAdapter, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCredentials();
    }

    // (temporary) update the textviews displaying user credentials on main menu
    public void updateCredentials() {
        user_Username = sharedPreferences.getString("username", "");
        user_Studentnumber = sharedPreferences.getString("studentnumber", "");
        tv_Username.setText(user_Username);
        tv_Studentnumber.setText(user_Studentnumber);
    }

    // onClick method for menu button at main menu on top right at toolbar
    public void popupmenuClicked(View view){
        startActivity(new Intent(this, menu_popup.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    // onClick method for BLE on/off switch at main menu
    public void BT_Switch(View view) {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
            Toast.makeText(this, R.string.toast_message, Toast.LENGTH_SHORT).show();
        }
    }

    // onClick method for clicking scan button at main menu
    public void BTScan(View view) {
        Utils.checkBluetooth(bluetoothAdapter, this);
        ble_scanner.startScan();
    }

    private void stopScan() {
        ble_scanner.stopScan();
    }


    public void BT_Discoverable(View view) {

        //permission check if device can advertise ble packets
        if( !BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported() ) {
            Toast.makeText( this, "Multiple advertisement not supported", Toast.LENGTH_SHORT ).show();
            Log.e("BLE", "Multiple ads not supported");
        }
        if(BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported() ) {
            Toast.makeText(this, "Multiple advertisement supported", Toast.LENGTH_SHORT).show();
            Log.e("BLE", "Multiple ads supported");


            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                    .setTimeout(5000)
                    .build();

            ParcelUuid self_uuid = new ParcelUuid(UUID.fromString(getString(R.string.ble_user_uuid)));
            //UUID set for advertising is 2e952a2b-eef3-4a80-a309-6a3f5aacb1e8
            AdvertiseData data = new AdvertiseData.Builder()
                    .setIncludeDeviceName(false)
                    .addServiceUuid(self_uuid)
                    .build();

            AdvertiseData advScanResponse = new AdvertiseData.Builder()
                    .setIncludeDeviceName(true)
                    .build();

            String user_uuid = getString(R.string.ble_user_uuid);


            AdvertiseCallback adCallback = new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                    Log.e(" BLE", "On start discovery success");
                    Toast.makeText(getApplicationContext(), "UUID is " + user_uuid + "\n Advertising set for 5 secs", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStartFailure(int errorCode) {
                    Log.e("BLE", "Advertising onStartFailure: " + errorCode);
                    super.onStartFailure(errorCode);
                }
            };


            bleadvertiser.startAdvertising(settings, data, advScanResponse, adCallback);
        }
    }
}