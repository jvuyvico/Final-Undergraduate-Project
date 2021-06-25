package com.feifei.testv4.ActivityPages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.feifei.testv4.SQLite.DatabaseAccess;
import com.feifei.testv4.R;
import com.feifei.testv4.ScanData_ListAdapter;
import com.feifei.testv4.Classes.Scan_Data;

import java.util.ArrayList;

public class ViewScanDataActivity extends AppCompatActivity {

    ListView lv_scanData;
    ArrayList<Scan_Data> scanData_AL;
    ScanData_ListAdapter scanDataListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_scan_data);

        lv_scanData = (ListView) findViewById(R.id.lv_scan_data);
        scanData_AL = new ArrayList<>();

        // load database info on the list on create
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        scanData_AL = databaseAccess.getScanData();
        databaseAccess.close();

        scanDataListAdapter = new ScanData_ListAdapter(this, R.layout.scan_data_list_item, scanData_AL);
        lv_scanData.setAdapter(scanDataListAdapter);
    }

}