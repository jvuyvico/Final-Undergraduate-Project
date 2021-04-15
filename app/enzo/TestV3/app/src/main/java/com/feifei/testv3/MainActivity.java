package com.feifei.testv3;

import android.Manifest;
import android.app.PendingIntent;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    /* Variable Declarations */
    public static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISABLE_BT = 0;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    BluetoothLeAdvertiser bleadvertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
    Beacon beaconble;
    BeaconParser beaconparserble;

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
    public static boolean credentialsinitialized = false;

    //for scanned BLE devices list
    ListView ble_listView;
    ArrayList<BLE_Device> ble_arrayList;
    BLE_ListAdapter ble_listAdapter;

    //for classes today list
    ListView classesToday_lv;
    public static TextView alarm_tv;
    public static ArrayList<User_Subject> classesToday_AL;
    Classes_ListAdapter classesListAdapter;

    /*
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    Toast.makeText(context, "YEEET", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

     */


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
        //registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);

        //Permission checks lang. Important though na i-approve mo yung permission request for location, so double check mo doon sa settings itself.
        int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
        }

        // Check if there is existing credentials in the CredentialsDB database
        if (!sharedPreferences.contains("initialized")) {
            Toast.makeText(this, "Please Set-up Credentials", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, AdminLoginActivity.class));
        } else {
            credentialsinitialized = sharedPreferences.getBoolean("initialized", true);
        }

        // Device list initialization
        ble_listView = (ListView) findViewById(R.id.DeviceList);
        ble_arrayList = new ArrayList<>();
        ble_listAdapter = new BLE_ListAdapter(this, R.layout.ble_device_list_item, ble_arrayList);
        ble_listView.setAdapter(ble_listAdapter);

        // add dummy device to check if list and listadapter is working
        BLE_Device dummydevice = new BLE_Device("DummyDevice", "46:BE:ED:BD:44:E5", "602EB8EB20EC04872040B4A52740CE18", "20xxX", "XXXX", "-50");
        ble_arrayList.add(dummydevice);
        ble_listAdapter.notifyDataSetChanged();

        // Classes today list initialization
        classesToday_lv = (ListView) findViewById(R.id.lv_ClassesToday);
        classesToday_AL = Utils.getClassesToday(this);
        classesListAdapter = new Classes_ListAdapter(this, R.layout.user_subject_list_item, classesToday_AL);
        classesToday_lv.setAdapter(classesListAdapter);

        // Check if bluetooth is turned on. If not, request.
        Utils.checkBluetooth(bluetoothAdapter, this);

        boolean alarmUp = (PendingIntent.getBroadcast(this, 20, new Intent(this, AlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp){
            Log.d("Alarm: ", "Alarm is already active");
        } else {
            AlarmSetter alarmSetter = new AlarmSetter(this , 20);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+1);
            calendar.set(Calendar.SECOND, 0);
            Intent intenttopass = new Intent(this, AlarmReceiver.class);
            alarmSetter.setAlarmManager(calendar, intenttopass);
            Log.d("Alarm: ", "Alarm set ");
        }



        /*
        ComponentName componentName = new ComponentName(this, ScanJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setPersisted(true)
                .setPeriodic(15*60*1000)    // set for 15 minutes
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("Job: ", "Job Scheduled");
        } else {
            Log.d("Job: ", "Job Scheduling Failed");
        }

         */

        

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


            bleadvertiser.startAdvertising(settings, data,  adCallback);
        }
    }
}