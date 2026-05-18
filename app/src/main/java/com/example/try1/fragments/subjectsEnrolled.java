package com.example.try1.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.try1.R;
import com.example.try1.adapaters.EnrolledSubjectAdapter;
import com.example.try1.databinding.FragmentSubjectsEnrolledBinding;
import com.example.try1.models.SubjectModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class subjectsEnrolled extends Fragment {
    FragmentSubjectsEnrolledBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<SubjectModel> list;

    public subjectsEnrolled() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSubjectsEnrolledBinding.inflate(inflater, container, false);

        EnrolledSubjectAdapter adapter = new EnrolledSubjectAdapter(getContext(), list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.EnrolledSubjectsRv.setLayoutManager(linearLayoutManager);


        database.getReference().child("user")
                .child(auth.getUid())
                .child("Enrolled").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            SubjectModel model = dataSnapshot.getValue(SubjectModel.class);
                            list.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.EnrolledSubjectsRv.setAdapter(adapter);

        return binding.getRoot();
    }
}