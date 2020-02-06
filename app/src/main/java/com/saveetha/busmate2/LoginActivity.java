package com.saveetha.busmate2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    PasswordManager pm;
    EditText username,password;
    Button signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        pm = new PasswordManager(this);
        if(pm.isLoggedIn()){
            startActivity(new Intent(this,MapsActivity.class));
            finish();
        }
        username = findViewById(R.id.usernameEditText);
        password = findViewById(R.id.passwordEditText);
        signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.signInButton){
            String usernameText = username.getText().toString();
            String passwordText = password.getText().toString();
            if(usernameText.equals("")){
                username.setError("Empty");
            }else if(passwordText.equals("")){
                password.setError("Empty");
            }else{
                if(pm.login(usernameText,passwordText)){
                    startActivity(new Intent(this,MapsActivity.class));
                    finish();
                }else{
                    username.setError("");
                    password.setError("");
                    signInButton.setText("Try Again!!!");
                }
            }
        }
    }
}
