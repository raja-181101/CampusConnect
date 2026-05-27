package com.example.try1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginpage extends AppCompatActivity {

    TextInputEditText emailEt, passwordEt;
    MaterialButton loginBtn;
    android.widget.TextView registerBtn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_loginpage);

        loginBtn = findViewById(R.id.login_btn);
        registerBtn = findViewById(R.id.register_btn);

        com.google.android.material.textfield.TextInputLayout emailLayout = findViewById(R.id.username_inputfield);
        com.google.android.material.textfield.TextInputLayout passLayout = findViewById(R.id.password_inputfield);
        emailEt = (TextInputEditText) emailLayout.getEditText();
        passwordEt = (TextInputEditText) passLayout.getEditText();

        loginBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString().trim();
            String pass = passwordEt.getText().toString().trim();
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            } else if (email.equals("mrp.admin@gmail.com") && pass.equals("123456789")) {
                startActivity(new Intent(this, admin.class));
                finish();
            }
            loginBtn.setEnabled(false);
            loginBtn.setText("Logging in...");
            auth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(r -> {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        loginBtn.setEnabled(true);
                        loginBtn.setText("Login");
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        registerBtn.setOnClickListener(v ->
            startActivity(new Intent(this, registerpage.class)));
    }
}
