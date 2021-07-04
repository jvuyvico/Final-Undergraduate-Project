package com.feifei.testv4.ActivityPages;

/*
    Activity that lists down classes stored in local offline SQLite database
    Can be accessed from the menu_popup list from MainActivity
 */

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.feifei.testv4.Classes.CourseSection;
import com.feifei.testv4.Classes.CourseTime;
import com.feifei.testv4.Classes_ListAdapter;
import com.feifei.testv4.SQLite.DatabaseAccess;
import com.feifei.testv4.JSON.JsonPlaceHolderApi;
import com.feifei.testv4.JSON.REST_Assign;
import com.feifei.testv4.JSON.REST_AssignTime;
import com.feifei.testv4.JSON.REST_Course;
import com.feifei.testv4.JSON.REST_CourseMapping;
import com.feifei.testv4.JSON.REST_Student;
import com.feifei.testv4.R;
import com.feifei.testv4.Classes.User_Subject;

import java.util.ArrayList;

public class ViewClassesActivity extends AppCompatActivity {

    ListView lv_classes;
    ArrayList<User_Subject> subjectArrayList;
    Classes_ListAdapter classesListAdapter;

    String studentnumber;

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
        studentnumber = databaseAccess.getStudentNumber();
        databaseAccess.close();


        classesListAdapter = new Classes_ListAdapter(this, R.layout.user_subject_list_item, subjectArrayList);
        lv_classes.setAdapter(classesListAdapter);
    }
}