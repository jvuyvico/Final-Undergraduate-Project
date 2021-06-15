package com.feifei.testv3;

/*
    Activity to set the credentials of a user in a single instance offline database using shared preferences
    Activity only accessible after completing AdminLoginActivity
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetCredentialsActivity extends AppCompatActivity {

    EditText inputUsername;
    EditText inputStudentnumber;
    Button backButton;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_credentials);
        backButton = (Button) findViewById(R.id.backButton);
        submitButton = (Button) findViewById(R.id.button_submit);
        inputUsername = findViewById(R.id.set_username);
        inputStudentnumber = findViewById(R.id.set_studentnumber);

    }

    // perform error checking to see if input from text is valid format of desired credentials information
    public void submitButtonClicked(View view){
        if(inputUsername.getText().toString().isEmpty() || inputStudentnumber.getText().length() != 9) {    //check if username is empty or invalid studentnumber
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            inputUsername.getText().clear();
            inputStudentnumber.getText().clear();
        } else {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
            String username = databaseAccess.getStudentUsername();
            if(username.contains("none")) {
                databaseAccess.insertStudentData(inputUsername.getText().toString(), inputStudentnumber.getText().toString());
            } else {
                Log.d("TAG", "submitButtonClicked: updating");
                databaseAccess.updateStudentData(inputUsername.getText().toString(), inputStudentnumber.getText().toString());
            }
            databaseAccess.close();

            Toast.makeText(this, "Credentials successfully set", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public void backButtonClicked(View view){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        String username = databaseAccess.getStudentUsername();
        databaseAccess.close();
        if(username.contains("none")) {
            Toast.makeText(this, "Please set-up credentials", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        String username = databaseAccess.getStudentUsername();
        databaseAccess.close();
        if(username.contains("none")) {
            Toast.makeText(this, "Please set-up credentials", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}