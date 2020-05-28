package com.indresh.mywhatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    MaterialEditText username,email,pass,repass;
    Button btn_register;
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        username=findViewById(R.id.username);
        email=findViewById(R.id.email);
        pass=findViewById(R.id.pass);
        repass=findViewById(R.id.repass);
        btn_register=findViewById(R.id.btn_register);
        auth=FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username=username.getText().toString();
                String txt_email=email.getText().toString();
                String txt_pass=pass.getText().toString();
                String txt_repass=repass.getText().toString();
                boolean email_pattern= Pattern.matches("[a-zA-z0-9_.]+@([a-z]+\\.)+[a-z]{2,4}$",txt_email);
                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_pass) || TextUtils.isEmpty(txt_pass) ||TextUtils.isEmpty(txt_repass)){
                    Toast.makeText(RegisterActivity.this, "All Fields are Required", Toast.LENGTH_SHORT).show();
                }else if(!email_pattern){
                    email.setError("Enter a Valid Email Address");
                    email.requestFocus();
                }else if(txt_pass.length()<8){
                    pass.setError("Password Must be of More Than Seven Characters");
                    pass.requestFocus();
                }else if(!txt_pass.equals(txt_repass)){
                    repass.setError("password Doesn't Match with above");
                    repass.requestFocus();
                }else{
                    register(txt_username,txt_email,txt_pass);
                }

            }
        });
    }

//add user to firebase database
    public void register(final String username, String email, String pass){
        auth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser=auth.getCurrentUser();
                            String userid=firebaseUser.getUid();
                            reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username",username);
                            hashMap.put("imageURL","default");
                            hashMap.put("status","offline");
                            hashMap.put("Search",username.toLowerCase());
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                 if (task.isSuccessful()){
                                     Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                     startActivity(intent);
                                     finish();
                                 }
                                }
                            });
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "You cant have this email or password for registration", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
