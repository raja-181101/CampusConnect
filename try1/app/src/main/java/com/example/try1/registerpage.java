package com.example.try1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.try1.models.stoaringdata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class registerpage extends AppCompatActivity {
    TextInputLayout username, phonenumber, email, password, confirmpassword;
    FirebaseDatabase firebasedatabase;
    DatabaseReference reference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerpage);
        username = findViewById(R.id.name);
        phonenumber = findViewById(R.id.mobile);
        email = findViewById(R.id.emails);
        password = findViewById(R.id.passwords);
        confirmpassword = findViewById(R.id.confirm);
        auth = FirebaseAuth.getInstance();
    }
    public void nextbuton(View view) {
        String username_ = username.getEditText().getText().toString();
        String phonenumber_ = phonenumber.getEditText().getText().toString();
        String email_ = email.getEditText().getText().toString();
        String password_ = password.getEditText().getText().toString();
        String confirmpassword_ = confirmpassword.getEditText().getText().toString();


        if (!username_.isEmpty()) {
            username.setError(null);
            username.setErrorEnabled(false);
            if (!phonenumber_.isEmpty()){
                phonenumber.setError(null);
                phonenumber.setErrorEnabled(false);
                if (!email_.isEmpty()){
                    email.setError(null);
                    email.setErrorEnabled(false);
                    if (email_.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")){
                        email.setError(null);
                        email.setErrorEnabled(false);
                        if (!password_.isEmpty()){
                            password.setError(null);
                            password.setErrorEnabled(false);
                            if (!confirmpassword_.isEmpty()){
                                confirmpassword.setError(null);
                                confirmpassword.setErrorEnabled(false);
                                if (confirmpassword_.equals(password_)){
                                    confirmpassword.setError(null);
                                    confirmpassword.setErrorEnabled(false);
                                    RadioGroup rg = (RadioGroup) findViewById(R.id.radiogroup);
                                    int selected = rg.getCheckedRadioButtonId();
                                    RadioButton rb = (RadioButton) findViewById(selected);

                                    firebasedatabase = FirebaseDatabase.getInstance();
                                    reference = firebasedatabase.getReference();
                                    auth.createUserWithEmailAndPassword(email_,password_).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()){
                                                String username_s = username.getEditText().getText().toString();
                                                String phonenumber_s = phonenumber.getEditText().getText().toString();
                                                String email_s = email.getEditText().getText().toString();
                                                String password_s = password.getEditText().getText().toString();
                                                String gender_s = rb.getText().toString();
                                                String id = task.getResult().getUser().getUid();
                                                stoaringdata stoaringdatass = new stoaringdata(id,username_s,phonenumber_s,email_s,password_s,gender_s);

                                                reference.child("user").child(id).setValue(stoaringdatass);
                                                Toast.makeText(getApplicationContext(),"regestration scucesses",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });


                                }else {
                                    confirmpassword.setError("pasword not match");
                                }
                            }else {
                                confirmpassword.setError("enter confirmpassword");
                            }
                        }else {
                            password.setError("fill the password");
                        }
                    }else {
                        email.setError("inncorrect email format");
                    }
                }else {
                    email.setError("enter the email");
                }
            }else {
                phonenumber.setError("enter mobile number");
            }

        }else {
            username.setError("enter username");
        }



    }
}