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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    /* ----------------------------- Variable Declarations -------------------------------------- */

    public static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISABLE_BT = 0;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

    private static final String TAG = "MainActivity";

    // interface interactables
    Button OnOff_button;
    TextView DailyAlarm_tv;
    ImageView popupmenu;

    //for user credentials
    SharedPreferences sharedPreferences;
    public String user_Username;
    public String user_Studentnumber;
    TextView Username_tv;
    TextView Studentnumber_tv;
    public static boolean credentialsinitialized = false;

    //for classes today list
    ListView classesToday_lv;
    public static TextView alarm_tv;
    public static ArrayList<User_Subject> classesToday_AL;
    Classes_ListAdapter classesListAdapter;

    //for BT Modes
    public static boolean BT_Mode = true; // true: listener, false: broadcaster
    Button BTMode_button;


    /* ------------------------------------------------------------------------------------------ */

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //run alarm set-up function in a background to ensure it doesnt get cancelled if app is closed
        new Thread(new Runnable(){
            @Override
            public void run() {
                boolean alarmUp = (PendingIntent.getBroadcast(MainActivity.this, 20, new Intent(MainActivity.this, AlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
                if (alarmUp){
                    Log.d("Alarm: ", "Alarm is already active");
                } else {
                    AlarmSetter alarmSetter = new AlarmSetter(MainActivity.this , 20);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND)+30);
                    Intent intenttopass = new Intent(MainActivity.this, AlarmReceiver.class);
                    alarmSetter.setAlarmManager(calendar, intenttopass);
                    Log.d("Main Alarm: ", "Alarm set");
                }
            }
        }).start();

        Username_tv = findViewById(R.id.tv_CredUsername);
        Studentnumber_tv = findViewById(R.id.tv_CredStudno);
        alarm_tv = findViewById(R.id.tv_AlarmSet);
        DailyAlarm_tv = findViewById(R.id.tv_DailyAlarm);
        BTMode_button = findViewById(R.id.but_BTMode);
        OnOff_button = findViewById(R.id.but_BTonoff);

        //Reset UI displays
        if (bluetoothAdapter.isEnabled()) {
            OnOff_button.setText("Turn BT off");
        }else {
            OnOff_button.setText("Turn BT on");
        }
        /**/
        boolean alarmUp = (PendingIntent.getBroadcast(MainActivity.this, 20, new Intent(MainActivity.this, AlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp){
            DailyAlarm_tv.setText("Daily alarm is active!");
        } else {
            DailyAlarm_tv.setText("Daily alarm is not active!");
        }
        /**/
        classesToday_lv = (ListView) findViewById(R.id.lv_ClassesToday);
        classesToday_AL = Utils.getClassesToday(this);
        classesListAdapter = new Classes_ListAdapter(this, R.layout.user_subject_list_item, classesToday_AL);
        classesToday_lv.setAdapter(classesListAdapter);
        /**/
        int alarmcounter = 0;
        for (int i = 0; i<classesToday_AL.size(); i++) {
            boolean classAlarmUp = (PendingIntent.getBroadcast(this, i, new Intent(this, ClassAlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
            if(classAlarmUp){
                alarmcounter++;
            }
        }
        alarm_tv.setText("Class Alarms set for today: " + String.valueOf(alarmcounter));

        //Permission checks lang. Important though na i-approve mo yung permission request for location, so double check mo doon sa settings itself.
        //for cleaning up
        /*
        int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
        }
         */
        this.requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1001);    // only works once forever, needs a workaround

        // Check if there is existing credentials in the CredentialsDB database
        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);
        if (!sharedPreferences.contains("initialized")) {
            Toast.makeText(this, "Please Set-up Credentials", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, AdminLoginActivity.class));
        } else {
            credentialsinitialized = sharedPreferences.getBoolean("initialized", true);
        }

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
        Username_tv.setText(user_Username);
        Studentnumber_tv.setText(user_Studentnumber);
    }

    // onClick method for menu button at main menu on top right at toolbar
    public void popupmenuClicked(View view){
        startActivity(new Intent(this, menu_popup.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void Alarm_Reset(View view) {
        boolean alarmUp = (PendingIntent.getBroadcast(MainActivity.this, 20, new Intent(MainActivity.this, AlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp){
            Log.d("Alarm: ", "Alarm is already active");
            Toast.makeText(this,"Alarm is already active!", Toast.LENGTH_SHORT).show();
        } else {
            AlarmSetter alarmSetter = new AlarmSetter(MainActivity.this , 20);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND)+30);
            Intent intenttopass = new Intent(MainActivity.this, AlarmReceiver.class);
            alarmSetter.setAlarmManager(calendar, intenttopass);
            Log.d("Main Alarm: ", "Alarm set");
        }
    }

    // onClick method for BLE on/off switch at main menu
    public void BT_OnOff(View view) {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            OnOff_button.setText("Turn BT off");
        }
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
            OnOff_button.setText("Turn BT on");
        }
    }

    public void BT_ModeSwitch(View view) {
        BT_Mode = !BT_Mode;
        if ( BT_Mode ) {
            BTMode_button.setText("Current Mode: Listener");
        } else {
            BTMode_button.setText("Current Mode: Broadcaster");
        }
    }
}