package com.feifei.testv3;

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
    String menu_list_items[] = new String[] {"Set Credentials", "Check My Classes", "Check My Attendance"};
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_popup);
        listView = (ListView) findViewById(R.id.menu_list);
        arrayAdapter = new ArrayAdapter(this, R.layout.popup_menu_list_item, menu_list_items );
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch((int) id) {
                    case 0:
                        startActivity(new Intent(menu_popup.this, AdminLoginActivity.class));
                        finish();
                        break;
                    case 1:
                        Toast.makeText(menu_popup.this, "Feature not available yet", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(menu_popup.this, "Feature not Available yet", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        width = dm.widthPixels;
        height = dm.heightPixels;

        getWindow().setLayout((int)(width*.7), (int)(height*.5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.END;
        params.x = 0;
        params.y = -245;

        getWindow().setAttributes(params);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}