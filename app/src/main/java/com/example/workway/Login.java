package com.example.workway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.workway.common_classes.AddUser;
import com.example.workway.common_classes.StatusBarColor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    static int Tries = 0;
    static double HowLong=5.0;
    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        StatusBarColor.ChangeColor(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        reff = FirebaseDatabase.getInstance().getReference().child("UserInfo");
    }

    public void GoBack(View v) {
        finish();
        startActivity(new Intent(this, Intro.class));
    }

    public void GoToResetPass(View v) {
        finish();
        startActivity(new Intent(this, ResetPassword.class));
    }

    public void GoToAdminLogin(View v) {
        finish();
        startActivity(new Intent(Login.this, AdminLogin.class));
    }

    public void GoToCompanyLogin(View v) {
        finish();
        startActivity(new Intent(Login.this, CompanyLogin.class));
    }

    public void CheckForLogin(View v) {
        EditText UserEmail = (EditText) findViewById(R.id.UserEmail);
        EditText UserPassword = (EditText) findViewById(R.id.UserPassword);
        if (isEmpty(UserEmail, UserPassword)) {
            Toast.makeText(Login.this, "Make sure text fields are not empty", Toast.LENGTH_SHORT).show();
        } else {
            if (Tries == 5) {
                TimerForTries.setTimer();
                TimerForTries.AlreadyBlocked=true;
                Toast.makeText(Login.this, "Blocked for : "+String.format("%.2f",HowLong)+" Minutes", Toast.LENGTH_SHORT).show();
            } else {
                login(UserEmail, UserPassword);
            }
        }
    }

    private boolean isEmpty(EditText UserEmail, EditText UserPassword) {
        return UserEmail.getText().toString().trim().isEmpty() || UserPassword.getText().toString().isEmpty();
    }

    private void login(EditText UserEmail, EditText UserPassword) {
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean IfUserExists = false;
                for (DataSnapshot data : snapshot.getChildren()) {
                    AddUser DB = data.getValue(AddUser.class);
                    if (UserEmail.getText().toString().equals(DB.Email) && UserPassword.getText().toString().equals(DB.Password)) {
                        IfUserExists = true;
                        MainScreen.WhichKey = Integer.parseInt(data.getKey());
                        if (DB.IfCVCreated == 0) {
                            MainScreen.IfCVCreatedBefore = false;
                        } else if (DB.IfCVCreated == 1) {
                            MainScreen.IfCVCreatedBefore = true;
                        }
                        break;
                    }
                }
                if (IfUserExists) {
                    //When the email and password are correct...
                    MainScreen.userEmail = UserEmail.getText().toString().trim();
                    finish();
                    MainActivity.ToWhere = MainScreen.class;
                    startActivity(new Intent(Login.this, MainActivity.class));
                } else {
                    Toast.makeText(Login.this, "Email or Password is incorrect", Toast.LENGTH_SHORT).show();
                    Tries++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}