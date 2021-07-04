package com.feifei.testv4;

/*
    List adapter for the ListView in ScanDevicesActivity which accepts BLE_Device class as entries
    Shouldn't be changed unless new elements are added to BLE_Device class
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feifei.testv4.Classes.BLE_Device;

import java.util.ArrayList;

public class BLE_ListAdapter extends ArrayAdapter<BLE_Device> {
    private static final String TAG = "ListAdapter_BLE_Devices";

    private Context mContext;
    int mResource;

    public BLE_ListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<BLE_Device> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        String name = getItem(position).getBLE_name();
        String address = getItem(position).getBLE_address();
        String uuid =  getItem(position).getBLE_uuid();
        String major = getItem(position).getBLE_major();
        String minor = getItem(position).getBLE_minor();
        String rssi = getItem(position).getBLE_rssi();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        TextView tvMacAddr = (TextView) convertView.findViewById(R.id.tv_macaddr);
        TextView tvUuid = (TextView) convertView.findViewById(R.id.tv_uuid);
        TextView tvMajor = (TextView) convertView.findViewById(R.id.tv_major);
        TextView tvMinor = (TextView) convertView.findViewById(R.id.tv_minor);
        TextView tvRssi = (TextView) convertView.findViewById(R.id.tv_rssi);

        tvName.setText(name);
        tvMacAddr.setText("Mac: " + address);
        tvUuid.setText("UUID: " + uuid);
        tvMajor.setText("Major: " + major);
        tvMinor.setText("Minor: " + minor);
        tvRssi.setText("RSSI: " + rssi);

        return convertView;
    }
}
