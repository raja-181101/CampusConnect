package com.example.try1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.try1.adapaters.commentAdapater;
import com.example.try1.adapaters.postadapter;
import com.example.try1.adapaters.questionAdapter;
import com.example.try1.models.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class questionsActivity extends AppCompatActivity {
    RecyclerView questRV;
    ArrayList<PostModel> list;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        progressBar= findViewById(R.id.questionprogress);

        questRV = findViewById(R.id.questionsRV);
        list = new ArrayList<>();

        questionAdapter questionAdapter = new questionAdapter(this, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        questRV.setLayoutManager(linearLayoutManager);
        questRV.setNestedScrollingEnabled(false);
        questRV.setAdapter(questionAdapter);

        database.getReference().child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    PostModel post = dataSnapshot.getValue(PostModel.class);
                    post.setPostid(dataSnapshot.getKey());
                    list.add(post);

                }
                questionAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}