package com.feifei.testv4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feifei.testv4.Classes.Attendance_Data;

import java.util.ArrayList;

public class AttendanceData_ListAdapter extends ArrayAdapter<Attendance_Data> {
    private Context mContext;
    int mResource;

    public AttendanceData_ListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Attendance_Data> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        String subject = getItem(position).getSubject();
        String status = getItem(position).getStatus();
        String uuid = getItem(position).getUuid();
        String major = getItem(position).getMajor();
        String minor = getItem(position).getMinor();
        String date = getItem(position).getDate();
        String time = getItem(position).getTime();

        Attendance_Data attendanceData = new Attendance_Data(subject, status, uuid, major, minor, date, time);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvSubject = (TextView) convertView.findViewById(R.id.tv_ad_subject);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.tv_ad_status);
        TextView tvUuid = (TextView) convertView.findViewById(R.id.tv_ad_uuid);
        TextView tvMajor = (TextView) convertView.findViewById(R.id.tv_ad_major);
        TextView tvMinor = (TextView) convertView.findViewById(R.id.tv_ad_minor);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_ad_date);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_ad_time);

        tvSubject.setText(subject);
        tvStatus.setText(status);
        tvUuid.setText(uuid);
        tvMajor.setText(major);
        tvMinor.setText(minor);
        tvDate.setText(date);
        tvTime.setText(time);

        return convertView;
    }
}
