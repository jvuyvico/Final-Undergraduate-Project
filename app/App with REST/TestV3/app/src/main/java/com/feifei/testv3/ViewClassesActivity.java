package com.feifei.testv3;

/*
    Activity that lists down classes stored in local offline SQLite database
    Can be accessed from the menu_popup list from MainActivity
 */

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

    public void refreshClicked(View view){
        // dummy button for testing
    }
}