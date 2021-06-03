package com.feifei.testv4;

/*
    Activity that lists down classes stored in local offline SQLite database
    Can be accessed from the menu_popup list from MainActivity
 */

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ViewClassesActivity extends AppCompatActivity {

    ListView lv_classes;
    ArrayList<User_Subject> subjectArrayList;
    Classes_ListAdapter classesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_classes);
        lv_classes = (ListView) findViewById(R.id.lv_classes);
        subjectArrayList = new ArrayList<>();


        // load database info on the list on create
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        subjectArrayList = databaseAccess.getData();
        databaseAccess.close();

        classesListAdapter = new Classes_ListAdapter(this, R.layout.user_subject_list_item, subjectArrayList);
        lv_classes.setAdapter(classesListAdapter);
    }
}