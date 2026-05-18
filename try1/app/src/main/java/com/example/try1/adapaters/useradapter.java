package com.example.try1.adapaters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.try1.R;
import com.example.try1.databinding.UserSampleBinding;
import com.example.try1.models.FollowModel;
import com.example.try1.models.stoaringdata;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class useradapter extends RecyclerView.Adapter<useradapter.ViewHolder> {
    Context context;
    ArrayList<stoaringdata> list;

    public useradapter(Context context, ArrayList<stoaringdata> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public useradapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_sample,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull useradapter.ViewHolder holder, int position) {
        stoaringdata user= list.get(position);
        Picasso.get()
                .load(user.getProfilephoto())
                .placeholder(R.drawable.profilepic)
                .into(holder.binding.userProfileImg);
        holder.binding.nameUser.setText(user.getUsername());
        holder.binding.AboutUser.setText("about");
        FirebaseDatabase.getInstance().getReference()
                        .child("user")
                                .child(user.getUserid())
                                        .child("followers")
                                                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            holder.binding.Follow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.followedbtn));
                            holder.binding.Follow.setText("Followed");
                            holder.binding.Follow.setTextColor(context.getResources().getColor(R.color.black));
                            holder.binding.Follow.setEnabled(false);

                        }else {
                            holder.binding.Follow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FollowModel follow = new FollowModel();
                                    follow.setFollowedBy(FirebaseAuth.getInstance().getUid());
                                    follow.setFollowedAt(new Date().getTime());

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("user")
                                            .child(user.getUserid())
                                            .child("followers")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(follow).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FollowModel follow = new FollowModel();
                                                    follow.setFollowingTo(user.getUserid());
                                                    follow.setFollowedAt(new Date().getTime());
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("user")
                                                            .child(FirebaseAuth.getInstance().getUid())
                                                            .child("following")
                                                            .child(user.getUserid())
                                                            .setValue(follow);
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("user")
                                                            .child(user.getUserid())
                                                            .child("followercount")
                                                            .setValue(user.getFollowercount()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("user")
                                                                            .child(FirebaseAuth.getInstance().getUid())
                                                                            .child("followingcount")
                                                                            .setValue(user.getFollowingcount()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    holder.binding.Follow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.followedbtn));
                                                                                    holder.binding.Follow.setText("Followed");
                                                                                    holder.binding.Follow.setTextColor(context.getResources().getColor(R.color.black));
                                                                                    holder.binding.Follow.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            });

                                }
                            });

                        }
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
            binding = UserSampleBinding.bind(itemView);
        }
    }
}
