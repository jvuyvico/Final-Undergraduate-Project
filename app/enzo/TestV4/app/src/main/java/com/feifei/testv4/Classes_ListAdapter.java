package com.feifei.testv4;

/*
    List adapter for the ListView in ViewClassesActivity which accepts User_Subject class as entries
    Shouldn't be changed unless new elements are added to User_Subject class
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feifei.testv4.Classes.User_Subject;

import java.util.ArrayList;

public class Classes_ListAdapter extends ArrayAdapter<User_Subject> {

    private Context mContext;
    int mResource;

    public Classes_ListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User_Subject> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        String subject = getItem(position).getSubject();
        String section = getItem(position).getSection();
        String days =  getItem(position).getDays();
        int timestart = getItem(position).getTimestart();
        int timeend = getItem(position).getTimeend();
        String uuid = getItem(position).getUuid();
        String major = getItem(position).getMajor();
        String minor = getItem(position).getMinor();
        String s_timestart = String.valueOf(timestart);
        String s_timeend = String.valueOf(timeend);

        User_Subject newclass = new User_Subject(subject, section, days, timestart, timeend, uuid, major, minor);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvSubject = (TextView) convertView.findViewById(R.id.tv_subject);
        TextView tvSection = (TextView) convertView.findViewById(R.id.tv_section);
        TextView tvDays = (TextView) convertView.findViewById(R.id.tv_days);
        TextView tvTimeStart = (TextView) convertView.findViewById(R.id.tv_timestart);
        TextView tvTimeEnd = (TextView) convertView.findViewById(R.id.tv_timeend);
        TextView tvUUID = (TextView) convertView.findViewById(R.id.tv_uuid);

        tvSubject.setText(subject);
        tvSection.setText(section);
        tvDays.setText(days);
        tvTimeStart.setText(s_timestart);
        tvTimeEnd.setText(s_timeend);
        tvUUID.setText("Beacon UUID: " + uuid);

        return convertView;
    }
}
