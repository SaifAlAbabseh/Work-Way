package com.example.workway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.view.ViewGroup;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.example.workway.common_classes.AddUser;
import com.example.workway.common_classes.StatusBarColor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {
    DatabaseReference reff;
    boolean IfExists=false;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        StatusBarColor.ChangeColor(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sign_up);
        EditText password=(EditText)findViewById(R.id.SignUserPassword);
        dialog=new Dialog(SignUp.this);
        dialog.setContentView(R.layout.pass_req_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.pass_req_dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations=R.style.pass_req_animation;
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userChangedPasswordTo=((EditText)findViewById(R.id.SignUserPassword)).getText().toString();
                CheckPassForReq req=new CheckPassForReq();
                if(userChangedPasswordTo.length()>=8 && userChangedPasswordTo.length()<=15){
                    TextView t=(TextView)dialog.findViewById(R.id.IfCorrectLength);
                    t.setTextColor(Color.GREEN);
                }
                else{
                    TextView t=(TextView)dialog.findViewById(R.id.IfCorrectLength);
                    t.setTextColor(Color.RED);
                }
                if(userChangedPasswordTo.contains(" ")){
                    TextView t=(TextView)dialog.findViewById(R.id.IfContainsSpaces);
                    t.setTextColor(Color.RED);
                }
                else{
                    TextView t=(TextView)dialog.findViewById(R.id.IfContainsSpaces);
                    t.setTextColor(Color.GREEN);
                }
                if(!req.IfThereACapitalLetter(userChangedPasswordTo)){
                    TextView t=(TextView)dialog.findViewById(R.id.IfContainsCapitalLetter);
                    t.setTextColor(Color.RED);
                }
                else{
                    TextView t=(TextView)dialog.findViewById(R.id.IfContainsCapitalLetter);
                    t.setTextColor(Color.GREEN);
                }
                if(!req.IfThereASmallLetter(userChangedPasswordTo)){
                    TextView t=(TextView)dialog.findViewById(R.id.IfContainsSmallLetter);
                    t.setTextColor(Color.RED);
                }
                else{
                    TextView t=(TextView)dialog.findViewById(R.id.IfContainsSmallLetter);
                    t.setTextColor(Color.GREEN);
                }
                if(!req.IfThereANumber(userChangedPasswordTo)){
                    TextView t=(TextView)dialog.findViewById(R.id.IfContainsNumber);
                    t.setTextColor(Color.RED);
                }
                else{
                    TextView t=(TextView)dialog.findViewById(R.id.IfContainsNumber);
                    t.setTextColor(Color.GREEN);
                }
                if(!req.IfThereASymbol(userChangedPasswordTo)){
                    TextView t=(TextView)dialog.findViewById(R.id.IfContainsSymbol);
                    t.setTextColor(Color.RED);
                }
                else{
                    TextView t=(TextView)dialog.findViewById(R.id.IfContainsSymbol);
                    t.setTextColor(Color.GREEN);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        reff= FirebaseDatabase.getInstance().getReference().child("UserInfo");
    }
    public void GoBack(View v) {
        finish();
        startActivity(new Intent(this,Intro.class));
    }

    public void CheckForSignUp(View v) {
        IfExists=false;
        if (isEmpty()) {
            Toast.makeText(SignUp.this,"Make sure text fields are not empty",Toast.LENGTH_SHORT).show();
        }
        else {
            if (isPasswordsEqual()) {
                CheckPassForReq check=new CheckPassForReq();
                EditText userEmail = (EditText) findViewById(R.id.SignUserEmail);
                EditText userPassword = (EditText) findViewById(R.id.SignUserPassword);
                if(check.CheckPassIfMeetTheReq(userPassword.getText().toString()).equals("good")){
                    signUp(userEmail, userPassword);
                }
                else{
                    Toast.makeText(SignUp.this,"Check the password requirements",Toast.LENGTH_SHORT).show();
                    dialog.show();
                }
            }
            else {
                Toast.makeText(SignUp.this,"Passwords are not the same",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signUp(EditText userEmail, EditText userPassword) {
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    AddUser DBEmail = data.getValue(AddUser.class);
                    if (userEmail.getText().toString().equals(DBEmail.Email)) {
                        IfExists = true;
                        break;
                    }
                }
                if (IfExists) {
                    Toast.makeText(SignUp.this, "Email is already in use", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                    CheckEmail.FromWhere = SignUp.this;
                    CheckEmail.SignUpPassword = userPassword.getText().toString();
                    CheckEmail.userEmail = userEmail.getText().toString().trim();
                    startActivity(new Intent(SignUp.this, CheckEmail.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private boolean isPasswordsEqual() {
        EditText UserPassword = (EditText) findViewById(R.id.SignUserPassword);
        EditText ConfirmPassword = (EditText) findViewById(R.id.SignConfirmPassword);
        return UserPassword.getText().toString().equals(ConfirmPassword.getText().toString());
    }

    private boolean isEmpty() {
        EditText UserEmail = (EditText) findViewById(R.id.SignUserEmail);
        EditText UserPassword = (EditText) findViewById(R.id.SignUserPassword);
        EditText ConfirmPassword = (EditText) findViewById(R.id.SignConfirmPassword);
        return UserEmail.getText().toString().trim().isEmpty() || UserPassword.getText().toString().isEmpty() || ConfirmPassword.getText().toString().isEmpty();
    }
    public void ShowPassReqDialog(View v){
        dialog.show();
    }
}