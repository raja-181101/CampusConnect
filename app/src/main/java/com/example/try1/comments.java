package com.example.try1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.try1.adapaters.commentAdapater;
import com.example.try1.databinding.ActivityCommentsBinding;
import com.example.try1.models.NotificationModel;
import com.example.try1.models.PostModel;
import com.example.try1.models.commentModel;
import com.example.try1.models.postlayoutmodel;
import com.google.gson.annotations.SerializedName;
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

public class comments extends AppCompatActivity {
    ActivityCommentsBinding binding;
    Intent intent;
    String postId;
    String postedBy;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ArrayList<commentModel> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        intent = getIntent();
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");






        database.getReference()
                .child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       PostModel post = snapshot.getValue(PostModel.class);
                        Picasso.get()
                                .load(post.getPostimage())
                                .placeholder(R.drawable.profilepic)
                                .into(binding.postimage);
                        binding.discription.setText(post.getPostdiscription());
                        binding.like.setText(post.getPostlikes()+"");
                        binding.comment.setText(post.getCommentcount()+"");
                        database.getReference().child("posts")
                                .child(postId)
                                .child("likes")
                                .child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like2, 0, 0, 0);

                                        }else{
                                            binding.like.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    database.getReference()
                                                            .child("posts")
                                                            .child(postId)
                                                            .child("likes")
                                                            .child(auth.getUid())
                                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    database.getReference()
                                                                            .child("posts")
                                                                            .child(postId)
                                                                            .child("postlikes")
                                                                            .setValue(post.getPostlikes()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like2, 0, 0, 0);
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
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        database.getReference()
                .child("user")
                .child(postedBy).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        stoaringdata user = snapshot.getValue(stoaringdata.class);
                        Picasso.get()
                                .load(user.getProfilephoto())
                                .placeholder(R.drawable.profile)
                                .into(binding.profileImg);
                        binding.postusername.setText(user.getUsername());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.commentsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             commentModel comment = new commentModel();
             comment.setCommentedBy(FirebaseAuth.getInstance().getUid());
             comment.setCommentBody(binding.commentinput.getText().toString());
             comment.setCommentedAt(new Date().getTime());

                database.getReference()
                        .child("posts")
                        .child(postId)
                        .child("comment")
                        .push()
                        .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference()
                                        .child("posts")
                                        .child(postId)
                                        .child("commentcount").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int commentcounts = 0;
                                                if (snapshot.exists()){
                                                    commentcounts = snapshot.getValue(Integer.class);

                                                }

                                                database.getReference()
                                                        .child("posts")
                                                        .child(postId)
                                                        .child("commentcount")
                                                        .setValue(commentcounts+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                binding.commentinput.setText("");
                                                                Toast.makeText(getApplication(),"commented",Toast.LENGTH_SHORT).show();

                                                                NotificationModel notification = new NotificationModel();
                                                                notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                notification.setNotificationAt(new Date().getTime());
                                                                notification.setPostId(postId);
                                                                notification.setPostedBy(postedBy);
                                                                notification.setType("comment");

                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("notifications")
                                                                        .child(postedBy)
                                                                        .push()
                                                                        .setValue(notification);

                                                            }
                                                        });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                            }
                        });

            }
        });

        commentAdapater adapater = new commentAdapater(this,list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.commentRV.setLayoutManager(layoutManager);
        binding.commentRV.setAdapter(adapater);

        database.getReference().child("posts")
                .child(postId)
                .child("comment").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            commentModel comment = dataSnapshot.getValue(commentModel.class);
                            list.add(comment);
                        }
                        adapater.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

}