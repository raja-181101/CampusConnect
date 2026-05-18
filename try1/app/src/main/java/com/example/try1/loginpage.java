package com.example.try1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class loginpage extends AppCompatActivity {
    Button registerbutton, loginbutton;

    TextInputLayout username, password;
    FirebaseAuth auth;
    FirebaseUser currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        auth = FirebaseAuth.getInstance();

        registerbutton = findViewById(R.id.register_btn);
        loginbutton = findViewById(R.id.login_btn);

        currentuser = auth.getCurrentUser();
        username = findViewById(R.id.username_inputfield);
        password = findViewById(R.id.password_inputfield);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_ = username.getEditText().getText().toString();
                String password_ = password.getEditText().getText().toString();

                if (!username_.isEmpty()) {
                    username.setError(null);
                    username.setErrorEnabled(false);

                    if (!password_.isEmpty()) {
                        password.setError(null);
                        password.setErrorEnabled(false);
                        if (username_.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")){
                            username.setError(null);
                            username.setErrorEnabled(false);

                            final String username_data = username.getEditText().getText().toString();
                            final String password_data = password.getEditText().getText().toString();


                            auth.signInWithEmailAndPassword(username_data,password_data).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        username.setError("user not exizt");
                                    }

                                }
                            });
                        }else {



/*
                            final String username_data = username.getEditText().getText().toString();
                            final String password_data = password.getEditText().getText().toString();

                            FirebaseDatabase firebasedatabase = FirebaseDatabase.getInstance();
                            DatabaseReference databasereference = firebasedatabase.getReference().child("user").child(auth.getUid());

                            databasereference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        stoaringdata user = snapshot.getValue(stoaringdata.class);
                                        String check_username = user.getUsername();
                                        if (check_username.equals(username_data)){
                                            username.setError(null);
                                            username.setErrorEnabled(false);
                                            String check_password = user.getPassword();
                                            if (check_password.equals(password_data)){
                                                password.setError(null);
                                                password.setErrorEnabled(false);
                                                Toast.makeText(getApplicationContext(), "login success", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }else {
                                                password.setError("incorrect pass");
                                            }
                                        }else {
                                            username.setError("invalid user");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });*/
                        }


                          }else {
                        password.setError("fill the password");
                    }

                } else {
                    username.setError("fill the inputfield");
                }
            }
        });


        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),registerpage.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentuser!=null){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
    }
}