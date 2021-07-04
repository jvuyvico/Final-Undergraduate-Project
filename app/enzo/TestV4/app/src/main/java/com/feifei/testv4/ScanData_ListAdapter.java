package com.feifei.testv4;

/*
    ListAdapter for Scan_Data class used in ViewScanDataActivity
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feifei.testv4.Classes.Scan_Data;

import java.util.ArrayList;

public class ScanData_ListAdapter extends ArrayAdapter<Scan_Data> {

    private Context mContext;
    int mResource;

    public ScanData_ListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Scan_Data> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        String uuid = getItem(position).getUuid();
        String time = getItem(position).getTime();
        String rssi =  getItem(position).getRssi();

        Scan_Data scanData = new Scan_Data(uuid, time, rssi);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvUuid = (TextView) convertView.findViewById(R.id.tv_sd_uuid);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_sd_time);
        TextView tvRssi = (TextView) convertView.findViewById(R.id.tv_sd_rssi);

        tvUuid.setText("Beacon UUID: " + uuid);
        tvTime.setText("Logged at: " + time);
        tvRssi.setText("RSSI: " + rssi);

        return convertView;
    }
}
