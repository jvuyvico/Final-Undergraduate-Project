package com.feifei.testv3;

/*
    Activity for Admin Login leading to SetCredentialsActivity.
    This activity is brought forward on initial run of application to set user credentials,
        after which it can be accessed from the menu_popup list from MainActivity
    Admin credentials currently hardcoded
 */


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminLoginActivity extends AppCompatActivity {

    EditText inputUsername;
    EditText inputPassword;
    Button backButton;
    Button loginButton;
    String validUsername;
    String validPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        backButton = (Button) findViewById(R.id.backButton);
        loginButton = (Button) findViewById(R.id.button_adminLogin);
        inputUsername = findViewById(R.id.admin_username);
        inputPassword = findViewById(R.id.admin_password);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        validUsername = databaseAccess.getAdminUsername();
        validPassword = databaseAccess.getAdminPassword();
        databaseAccess.close();

        Log.d("lol", validPassword + validUsername);

    }

    public void loginButtonClicked(View view){
        if(inputUsername.getText().toString().equals(validUsername) && inputPassword.getText().toString().equals(validPassword)) {
            startActivity(new Intent(AdminLoginActivity.this, SetCredentialsActivity.class));
            finish();

        } else {
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            inputUsername.getText().clear();
            inputPassword.getText().clear();
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