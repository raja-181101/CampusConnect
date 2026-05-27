package com.example.try1.adapaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.try1.R;
import com.example.try1.databinding.SubjectSampleLayoutBinding;
import com.example.try1.models.SubjectModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
    Context context;
    ArrayList<SubjectModel> list;

    public SubjectAdapter(Context context, ArrayList<SubjectModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subject_sample_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectAdapter.ViewHolder holder, int position) {

        SubjectModel model = list.get(position);
        holder.binding.SubjectName.setText(model.getSubjectName());
        holder.binding.Branch.setText(model.getBranch());
        holder.binding.Year.setText(model.getYear());

        holder.binding.subjectCardId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.binding.InputSubKey.setVisibility(View.VISIBLE);
                holder.binding.key.setVisibility(View.VISIBLE);
                holder.binding.key.setClickable(true);
                holder.binding.AccessNow.setText("Enroll");
            }
        });




            holder.binding.AccessNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Subjects")
                            .child(model.getSubjectName())
                            .child("Sections").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot  : snapshot.getChildren()){
                                        SubjectModel sube = dataSnapshot.getValue(SubjectModel.class);
                                        if (holder.binding.key.getText().toString().equals(sube.getSubjectkey())){
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("Subjects")
                                                    .child(model.getSubjectName())
                                                    .child("Sections")
                                                    .child(sube.getSec())
                                                    .child("Enrolled")
                                                    .child(FirebaseAuth.getInstance().getUid())
                                                    .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            SubjectModel enmod = new SubjectModel();
                                                            enmod.setSubjectName(model.getSubjectName());
                                                            enmod.setSec(sube.getSec());
                                                            enmod.setYear(model.getYear());
                                                            enmod.setBranch(model.getBranch());
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child("user")
                                                                    .child(FirebaseAuth.getInstance().getUid())
                                                                    .child("Enrolled")
                                                                    .child(model.getSubjectName())
                                                                    .setValue(enmod).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            holder.binding.InputSubKey.getEditText().setText("");
                                                                            holder.binding.InputSubKey.setVisibility(View.GONE);
                                                                            holder.binding.key.setClickable(false);
                                                                            holder.binding.key.setVisibility(View.GONE);

                                                                            Toast.makeText(context, "Sucess", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });

                                                        }
                                                    });
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                }
            });






    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SubjectSampleLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = SubjectSampleLayoutBinding.bind(itemView);
        }
    }
}
