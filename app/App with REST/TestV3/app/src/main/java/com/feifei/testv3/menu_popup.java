package com.feifei.testv3;

/*
    Activity seen when menu icon from toolbar in MainActivity is clicked
 */

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class menu_popup extends Activity {

    int width;
    int height;
    ListView listView;
    String menu_list_items[] = new String[] {"Set Credentials", "Check My Classes", "Check My Attendance", "REST Test"};
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_popup);
        listView = (ListView) findViewById(R.id.menu_list);
        arrayAdapter = new ArrayAdapter(this, R.layout.popup_menu_list_item, menu_list_items );
        listView.setAdapter(arrayAdapter);

        // Link activities here, using switch cases to determine which list item was clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch((int) id) {
                    case 0:
                        startActivity(new Intent(menu_popup.this, AdminLoginActivity.class));
                        finish();
                        break;
                    case 1:
                        startActivity(new Intent(menu_popup.this, ViewClassesActivity.class));
                        finish();
                        break;
                    case 2:
                        Toast.makeText(menu_popup.this, "Feature not Available yet", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        startActivity(new Intent(menu_popup.this, rest_test.class));
                        finish();
                        break;
                }
            }
        });

        /* Edit view and scaling of the 'pop up' menu */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        width = dm.widthPixels;
        height = dm.heightPixels;

        getWindow().setLayout((int)(width*.6), (int)(height*.5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.END;
        params.x = 0;
        params.y = (int) (-height*0.18);

        getWindow().setAttributes(params);
        /* ------------------------------------------- */
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}