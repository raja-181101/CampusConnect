package com.example.try1.adapaters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.try1.databinding.PostRecyclearviewBinding;
import com.example.try1.R;
import com.example.try1.comments;
import com.example.try1.models.NotificationModel;
import com.example.try1.models.PostModel;
import com.example.try1.models.postlayoutmodel;
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

public class postadapter extends RecyclerView.Adapter<postadapter.ViewHolder> {

    Context context;
    ArrayList<PostModel> list;


    public postadapter(Context context, ArrayList<PostModel> list) {
        this.context = context;
        this.list = list;
    }

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public postadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_recyclearview, parent, false);


        return new ViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull postadapter.ViewHolder holder, int position) {

        PostModel model = list.get(position);
        Picasso.get()
                .load(model.getPostimage())
                .placeholder(R.drawable.profilepic)
                .into(holder.binding.PostImage);
        holder.binding.like.setText(model.getPostlikes() + "");
        holder.binding.comment.setText(model.getCommentcount()+"");
        String discription = model.getPostdiscription();
        if (discription.equals("")) {
            holder.binding.discription.setVisibility(View.GONE);
        } else {
            holder.binding.discription.setText(model.getPostdiscription());
            holder.binding.discription.setVisibility(View.VISIBLE);
        }
        FirebaseDatabase.getInstance().getReference().child("user")
                .child(model.getPostedby()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        stoaringdata users = snapshot.getValue(stoaringdata.class);
                        Picasso.get()
                                .load(users.getProfilephoto())
                                .placeholder(R.drawable.profilepic)
                                .into(holder.binding.postProfileImg);
                        holder.binding.postUserName.setText(users.getUsername());
                        holder.binding.AboutPost.setText("ECE");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(model.getPostid()).child("likes")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like2, 0, 0, 0);

                        } else {
                            holder.binding.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("posts")
                                            .child(model.getPostid())
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("posts")
                                                            .child(model.getPostid())
                                                            .child("postlikes")
                                                            .setValue(model.getPostlikes() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like2, 0, 0, 0);


                                                                    NotificationModel notification  = new NotificationModel();
                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notification.setNotificationAt(new Date().getTime());
                                                                    notification.setType("like");
                                                                    notification.setPostedBy(model.getPostedby());
                                                                    notification.setPostId(model.getPostid());

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notifications")
                                                                            .child(model.getPostedby())
                                                                            .push()
                                                                            .setValue(notification);

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
        holder.binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, comments.class);
                intent.putExtra("postId",model.getPostid());
                intent.putExtra("postedBy",model.getPostedby());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       PostRecyclearviewBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = PostRecyclearviewBinding.bind(itemView);


        }
    }
}
