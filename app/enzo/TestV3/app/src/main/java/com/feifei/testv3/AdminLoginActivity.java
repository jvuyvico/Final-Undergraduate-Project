package com.feifei.testv3;

/*
    Activity for Admin Login leading to SetCredentialsActivity.
    Admin credentials currently hardcoded
 */


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminLoginActivity extends AppCompatActivity {

    EditText inputUsername;
    EditText inputPassword;
    Button backButton;
    Button loginButton;
    private final String validUsername = "qwe"; //hardcoded for now
    private final String validPassword = "123"; //hardcoded for now

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        backButton = (Button) findViewById(R.id.backButton);
        loginButton = (Button) findViewById(R.id.button_adminLogin);
        inputUsername = findViewById(R.id.admin_username);
        inputPassword = findViewById(R.id.admin_password);

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
        finish();
    }

}