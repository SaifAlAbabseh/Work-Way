package com.example.workway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.workway.common_classes.RegisterCompany;
import com.example.workway.common_classes.StatusBarColor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompanyLogin extends AppCompatActivity {
    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        StatusBarColor.ChangeColor(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_company_login);
        reff= FirebaseDatabase.getInstance().getReference().child("CompanyInfo");
    }
    public void GoBack(View v){
        finish();
        startActivity(new Intent(this,Login.class));
    }
    public void CheckForCompanyLogin(View v){
        EditText ID=(EditText)findViewById(R.id.EnteredID);
        EditText password=(EditText)findViewById(R.id.enteredcompanypassword);
        if(!ID.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty()){
            CheckForCompanyLoginHelper(ID, password);
        }
        else{
            Toast.makeText(CompanyLogin.this,"Fields cannot be empty",Toast.LENGTH_SHORT).show();
        }
    }

    private void CheckForCompanyLoginHelper(EditText ID, EditText password){
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean IfExists=false;
                for(DataSnapshot data:snapshot.getChildren()){
                    RegisterCompany com=data.getValue(RegisterCompany.class);
                    if(ID.getText().toString().equals(com.ID) && password.getText().toString().equals(com.Password)){
                        IfExists=true;
                        break;
                    }
                }
                if(IfExists){
                    Company_Main.ID=ID.getText().toString();
                    finish();
                    MainActivity.ToWhere=Company_Main.class;
                    startActivity(new Intent(CompanyLogin.this,MainActivity.class));
                }
                else{
                    Toast.makeText(CompanyLogin.this,"Invalid information",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}