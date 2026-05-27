package com.example.try1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.try1.databinding.ActivityMainBinding;
import com.example.try1.fragments.*;
import com.example.try1.models.NotificationModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding binding;
    FirebaseAuth auth;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomnav;
    Toolbar toolbar;
    ImageView questionButton, chatButton;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomnav = binding.appBarId.includeContainerId.bottomNav;
        drawerLayout = binding.Drawer;
        navigationView = binding.NavigationView;
        toolbar = binding.appBarId.toolBar;
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        // Set current user online
        database.getReference("presence").child(auth.getUid()).setValue(true);
        database.getReference("presence").child(auth.getUid()).onDisconnect().setValue(false);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.OpenDrawer, R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomnav.setOnNavigationItemSelectedListener(this);
        bottomnav.setSelectedItemId(R.id.home);

        // Question button in toolbar
        if (binding.appBarId.questionButton != null) {
            binding.appBarId.questionButton.setOnClickListener(v ->
                startActivity(new Intent(this, questionsActivity.class)));

        }

        // Chat button in toolbar
        if (binding.appBarId.chatButton != null) {
            binding.appBarId.chatButton.setOnClickListener(v ->
                startActivity(new Intent(this, ChatListActivity.class)));
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.subjects) {
                startActivity(new Intent(this, subject.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        });

        // Notification badge
        database.getReference("notifications").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        BadgeDrawable badge = bottomnav.getOrCreateBadge(R.id.notification);
                        int unread = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            NotificationModel model = ds.getValue(NotificationModel.class);
                            if (model != null && !model.isCheckOpen()) unread++;
                        }
                        if (unread > 0) {
                            badge.setVisible(true);
                            badge.setNumber(unread);
                        } else {
                            badge.setVisible(false);
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    homefragment homeFrag = new homefragment();
    notificationfragment notifFrag = new notificationfragment();
    searchfragment searchFrag = new searchfragment();
    uploadfragment uploadFrag = new uploadfragment();
    profilefragment profileFrag = new profilefragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        int id = item.getItemId();
        if (id == R.id.home) tx.replace(R.id.container, homeFrag).commit();
        else if (id == R.id.search) tx.replace(R.id.container, searchFrag).commit();
        else if (id == R.id.upload) tx.replace(R.id.container, uploadFrag).commit();
        else if (id == R.id.notification) tx.replace(R.id.container, notifFrag).commit();
        else if (id == R.id.profile) tx.replace(R.id.container, profileFrag).commit();
        else return false;
        return true;
    }

    public void logout_btm(View view) {
        auth.signOut();
        database.getReference("presence").child(auth.getUid()).setValue(false);
        startActivity(new Intent(this, loginpage.class));
        finish();
    }
    public void logout_btm2(View view) {
        auth.signOut();
        database.getReference("presence").child(auth.getUid()).setValue(false);
        startActivity(new Intent(this, loginpage.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
