package com.feifei.testv3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class CheckAttendanceActivity extends AppCompatActivity {

    ListView lv_attendanceData;
    ArrayList<Attendance_Data> attendanceData_AL;
    AttendanceData_ListAdapter attendanceDataListAdapter;
    Button AD_refresh_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);
        AD_refresh_button = findViewById(R.id.but_CA_refresh);

        lv_attendanceData = (ListView) findViewById(R.id.lv_attendance_data);
        attendanceData_AL = new ArrayList<>();

        // load database info on the list on create
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        attendanceData_AL = databaseAccess.getAttendanceData();
        databaseAccess.close();

        attendanceDataListAdapter = new AttendanceData_ListAdapter(this, R.layout.attendance_data_list_item, attendanceData_AL);
        lv_attendanceData.setAdapter(attendanceDataListAdapter);
    }

    public void CA_refreshClicked( View view ) {
        attendanceData_AL.clear();
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        attendanceData_AL.addAll(databaseAccess.getAttendanceData());
        databaseAccess.close();
        attendanceDataListAdapter.notifyDataSetChanged();
    }
}