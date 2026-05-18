package com.example.try1.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.try1.R;
import com.example.try1.adapaters.postadapter;
import com.example.try1.models.PostModel;
import com.example.try1.questionsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class homefragment extends Fragment {
    RecyclerView postrv;
    ArrayList<PostModel> list;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ProgressBar progressBar;
    ImageView img;


    public homefragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_homefragment, container, false);
progressBar= view.findViewById(R.id.progressid);

        postrv = view.findViewById(R.id.postRV);
        list = new ArrayList<>();
      //  img = view.findViewById(R.id.questionButton);

        postadapter postadapter = new postadapter(getContext(), list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        postrv.setLayoutManager(linearLayoutManager);
        postrv.setNestedScrollingEnabled(false);
        postrv.setAdapter(postadapter);

        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    PostModel post = dataSnapshot.getValue(PostModel.class);
                    post.setPostid(dataSnapshot.getKey());
                    list.add(0,post);

                }
                postadapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        postrv.smoothScrollToPosition(0);


     /*   img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), questionsActivity.class);
                startActivity(intent);
            }
        });*/




        return view;


    }

}