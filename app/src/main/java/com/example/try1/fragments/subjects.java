package com.example.try1.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.try1.R;
import com.example.try1.adapaters.SubjectAdapter;
import com.example.try1.databinding.FragmentSubjectsBinding;
import com.example.try1.models.SubjectModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class subjects extends Fragment {
    FragmentSubjectsBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<SubjectModel> allSubjects,list;
    ArrayList<String> enrolledSubjects;
    ArrayList<SubjectModel> list3;

    public subjects() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        list = new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSubjectsBinding.inflate(inflater, container, false);

        SubjectAdapter adapter = new SubjectAdapter(getContext(), list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.SubjectsRv.setLayoutManager(linearLayoutManager);

        database.getReference().child("Subjects").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allSubjects = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    SubjectModel model = dataSnapshot.getValue(SubjectModel.class);
                    allSubjects.add(model);
                }
                database.getReference().child("user").child(auth.getUid()).child("Enrolled").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        enrolledSubjects = new ArrayList<>();
                        if (snapshot.exists()){
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                SubjectModel model = dataSnapshot.getValue(SubjectModel.class);
                                enrolledSubjects.add(model.getSubjectName());
                            }
                        }
                        list.clear();
                        for (SubjectModel model : allSubjects){
                            if (!enrolledSubjects.contains(model.getSubjectName())){
                                list.add(model);

                            }
                        }
                        adapter.notifyDataSetChanged();

                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




       /*database.getReference().child("Subjects").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    SubjectModel model = dataSnapshot.getValue(SubjectModel.class);
                    final String[] Sub = new String[1];


                //list.add(model);
                   database.getReference().child("user").child(auth.getUid()).child("Enrolled").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {

                                    SubjectModel model1 = dataSnapshot1.getValue(SubjectModel.class);

                                    if (model1.getSubjectName().equals(model.getSubjectName())) {
                                        Sub[0] = model1.getSubjectName();

                                    }

                                }
                                    if (!model.getSubjectName().equals(Sub[0])) {
                                        list.add(model);
                                        adapter.notifyDataSetChanged();


                                    }



                            } else {
                                list.add(model);
                                adapter.notifyDataSetChanged();

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
                adapter.notifyDataSetChanged();



            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        binding.SubjectsRv.setAdapter(adapter);


        return binding.getRoot();
    }
}