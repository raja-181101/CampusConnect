package com.example.try1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.try1.databinding.ActivityMainBinding;
import com.example.try1.fragments.homefragment;
import com.example.try1.fragments.notificationfragment;
import com.example.try1.fragments.profilefragment;
import com.example.try1.fragments.searchfragment;
import com.example.try1.fragments.uploadfragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding binding;
    Button logout;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding  = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNav.setOnNavigationItemSelectedListener(this);
        binding.bottomNav.setSelectedItemId(R.id.home);
        auth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logout_btn);






    }
    public void logout(View view){
        auth.signOut();
        Intent intent = new Intent(getApplicationContext(),loginpage.class);
        startActivity(intent);
        finish();
        Toast.makeText(getApplicationContext(),"logout",Toast.LENGTH_SHORT).show();
    }
    com.example.try1.fragments.homefragment homefragment = new homefragment();
    com.example.try1.fragments.notificationfragment notificationfragment = new notificationfragment();
    com.example.try1.fragments.searchfragment searchfragment = new searchfragment();
    com.example.try1.fragments.uploadfragment uploadfragment = new uploadfragment();
    com.example.try1.fragments.profilefragment profilefragment = new profilefragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()){
            case R.id.home:
                transaction.replace(R.id.container,homefragment).commit();
                return true;
            case R.id.search:
                transaction.replace(R.id.container,searchfragment).commit();
                return true;
            case R.id.upload:
                transaction.replace(R.id.container,uploadfragment).commit();
                return true;
            case R.id.notification:
                transaction.replace(R.id.container,notificationfragment).commit();
                return true;
            case R.id.profile:
                transaction.replace(R.id.container,profilefragment).commit();
                return true;
        }

        return false;
    }
    public void logout_btm(View view) {
        auth.signOut();
        startActivity(new Intent(getApplicationContext(),loginpage.class));
        finish();
    }


}