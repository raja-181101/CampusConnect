package com.example.try1.adapaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.try1.R;
import com.example.try1.databinding.UserSampleBinding;
import com.example.try1.models.FollowModel;
import com.example.try1.models.stoaringdata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {
    ArrayList<FollowModel> list;
    Context context;

    public FollowAdapter(ArrayList<FollowModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public FollowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_sample,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowAdapter.ViewHolder holder, int position) {
        FollowModel model = list.get(position);
        FirebaseDatabase.getInstance().getReference()
                .child("user")
                .child(model.getFollowedBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        stoaringdata stoaringdata = snapshot.getValue(stoaringdata.class);
                        Picasso.get()
                                .load(stoaringdata.getProfilephoto())
                                .placeholder(R.drawable.profilepic)
                                .into(holder.binding.userProfileImg);
                        holder.binding.nameUser.setText(stoaringdata.getUsername());
                        holder.binding.Follow.setText("Follow Back");



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        UserSampleBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding  = UserSampleBinding.bind(itemView);
        }
    }
}
