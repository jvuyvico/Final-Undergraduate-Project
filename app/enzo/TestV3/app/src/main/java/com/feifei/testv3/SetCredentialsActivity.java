package com.feifei.testv3;

/*
    Activity to set the credentials of a user in a single instance offline database.
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetCredentialsActivity extends AppCompatActivity {

    EditText inputUsername;
    EditText inputStudentnumber;
    Button backButton;
    Button submitButton;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    //public static UserCredentials userCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_credentials);
        backButton = (Button) findViewById(R.id.backButton);
        submitButton = (Button) findViewById(R.id.button_submit);
        inputUsername = findViewById(R.id.set_username);
        inputStudentnumber = findViewById(R.id.set_studentnumber);

        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE); //change MODE_PRIVATE later
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    public void submitButtonClicked(View view){
        if(inputUsername.getText().toString().isEmpty() || inputStudentnumber.getText().length() != 9) {    //check if username is empty or invalid studentnumber
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            inputUsername.getText().clear();
            inputStudentnumber.getText().clear();
        } else {
            //userCredentials = new UserCredentials(inputUsername.getText().toString(), inputStudentnumber.getText().toString());

            sharedPreferencesEditor.putString("username", inputUsername.getText().toString());
            sharedPreferencesEditor.putString("studentnumber", inputStudentnumber.getText().toString());
            sharedPreferencesEditor.apply();

            Toast.makeText(this, "Credentials successfully set", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public void backButtonClicked(View view){
        finish();
    }
}