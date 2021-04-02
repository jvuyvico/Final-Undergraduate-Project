package com.feifei.testv3;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISABLE_BT = 0;

    private static final String TAG = "MainActivity";
    private Scanner ble_scanner;
    Button BTScan;
    Button TALButton;

    SharedPreferences sharedPreferences;
    public String user_Username;
    public String user_Studentnumber;
    TextView tv_Username;
    TextView tv_Studentnumber;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

    /* Device list variables declaration */
    /*
    ArrayList arrayList;
    ListView mlistofDevices;
    ArrayAdapter<String> arrayAdapter;
     */
    ListView ble_listView;
    ArrayList<BLE_Device> ble_arrayList;
    BLE_ListAdapter ble_listAdapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BTScan = (Button) findViewById(R.id.BT_Scan);
        TALButton = (Button) findViewById(R.id.button_adminLogin);
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

        if (!sharedPreferences.contains("username")) {
            Toast.makeText(this, "Please Set-up Credentials", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, AdminLoginActivity.class));
        }

        /* Device list variables initialization */
        /*
        mlistofDevices = (ListView) findViewById(R.id.DeviceList);
        arrayList = new ArrayList();
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,arrayList);
        mlistofDevices.setAdapter(arrayAdapter);
         */

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

    public void updateCredentials() {
        user_Username = sharedPreferences.getString("username", "");
        user_Studentnumber = sharedPreferences.getString("studentnumber", "");
        tv_Username.setText(user_Username);
        tv_Studentnumber.setText(user_Studentnumber);
    }

    public void TALButtonClicked(View view) {   // configure toAdminLoginButton
        startActivity(new Intent(MainActivity.this, AdminLoginActivity.class));
        updateCredentials();
    }


    public void BT_Switch(View view) { //function linked to switch on or of button.
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        }
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
            Toast.makeText(this, R.string.toast_message, Toast.LENGTH_SHORT).show();
        }
    }

    public void BTScan(View view) {
        Utils.checkBluetooth(bluetoothAdapter, this);
        ble_scanner.startScan();
    }

    private void stopScan() {
        ble_scanner.stopScan();
    }
}