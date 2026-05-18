package com.example.try1.adapaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.try1.Notes;
import com.example.try1.R;
import com.example.try1.databinding.FragmentSubjectsEnrolledBinding;
import com.example.try1.databinding.SubjectSampleLayoutBinding;
import com.example.try1.models.SubjectModel;

import java.util.ArrayList;

public class EnrolledSubjectAdapter extends RecyclerView.Adapter<EnrolledSubjectAdapter.ViewHolder> {

    Context context;
    ArrayList<SubjectModel> list;

    public EnrolledSubjectAdapter(Context context, ArrayList<SubjectModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public EnrolledSubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subject_sample_layout,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull EnrolledSubjectAdapter.ViewHolder holder, int position) {

        SubjectModel model = list.get(position);
        holder.binding.AccessNow.setVisibility(View.GONE);
        holder.binding.SubjectName.setText(model.getSubjectName());
        holder.binding.Branch.setText(model.getBranch());
        holder.binding.Year.setText(model.getSec());
        holder.binding.SubjectName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Notes.class);
                intent.putExtra("subname",model.getSubjectName());
                intent.putExtra("sec",model.getSec());
                intent.putExtra("branch",model.getBranch());
                context.startActivity(intent);


                Toast.makeText(context, "name: "+model.getSubjectName(), Toast.LENGTH_SHORT).show();
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