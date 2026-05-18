package com.example.try1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.try1.adapaters.AnswerAdapter;
import com.example.try1.databinding.ActivityAnswersBinding;
import com.example.try1.databinding.ActivityCommentsBinding;
import com.example.try1.models.NotificationModel;
import com.example.try1.models.PostModel;
import com.example.try1.models.commentModel;
import com.example.try1.models.stoaringdata;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class answers extends AppCompatActivity {
    ActivityAnswersBinding binding;

    Intent intent;
    String postId;
    String postedBy;

    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;
    Uri uri;
    ProgressDialog dialog;

    ArrayList<commentModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnswersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog= new ProgressDialog(this);

        dialog.setTitle("Uploading");
        dialog.setMessage("please wait..");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        intent = getIntent();
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");


        database.getReference()
                .child("user")
                .child(postedBy).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        stoaringdata user = snapshot.getValue(stoaringdata.class);
                        Picasso.get()
                                .load(user.getProfilephoto())
                                .placeholder(R.drawable.profile)
                                .into(binding.QuestionImage);
                        binding.questionusername.setText(user.getUsername());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference()
                .child("questions")
                .child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        PostModel post = snapshot.getValue(PostModel.class);
                        String img = post.getPostimage();
                        if (img == null) {
                            binding.questionimage.setVisibility(View.GONE);
                            binding.Question.setText(post.getPostdiscription());
                            binding.like.setText(post.getPostlikes() + "");
                            binding.comment.setText(post.getCommentcount() + "");
                        }else {
                            Picasso.get()
                                    .load(post.getPostimage())
                                    .placeholder(R.drawable.profilepic)
                                    .into(binding.questionimage);
                            binding.Question.setText(post.getPostdiscription());
                            binding.like.setText(post.getPostlikes()+"");
                            binding.comment.setText(post.getCommentcount()+"");
                            binding.questionimage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), imgshow.class);
                                    intent.putExtra("imgurl", post.getPostimage());
                                    startActivity(intent);
                                }
                            });
                        }
                        database.getReference().child("questions")
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
                                                            .child("questions")
                                                            .child(postId)
                                                            .child("likes")
                                                            .child(auth.getUid())
                                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    database.getReference()
                                                                            .child("questions")
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


       binding.commentinput.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               String ans = binding.commentinput.getText().toString();
               if (!ans.isEmpty()){
                   binding.answerSend.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           dialog.show();

                           final StorageReference reference = storage.getReference().child("commentanswer")
                                   .child(auth.getUid())
                                   .child(new Date().getTime()+"");

                           if(uri == null){
                               commentModel comment = new commentModel();
                               comment.setCommentedBy(FirebaseAuth.getInstance().getUid());
                               comment.setCommentBody(binding.commentinput.getText().toString());
                               comment.setCommentedAt(new Date().getTime());

                               database.getReference()
                                       .child("questions")
                                       .child(postId)
                                       .child("comment")
                                       .push()
                                       .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void unused) {
                                               database.getReference()
                                                       .child("questions")
                                                       .child(postId)
                                                       .child("commentcount").addListenerForSingleValueEvent(new ValueEventListener() {
                                                           @Override
                                                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                               int commentcounts = 0;
                                                               if (snapshot.exists()) {
                                                                   commentcounts = snapshot.getValue(Integer.class);

                                                               }

                                                               database.getReference()
                                                                       .child("questions")
                                                                       .child(postId)
                                                                       .child("commentcount")
                                                                       .setValue(commentcounts + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                           @Override
                                                                           public void onSuccess(Void unused) {
                                                                               binding.commentinput.setText("");
                                                                               dialog.dismiss();
                                                                               Toast.makeText(getApplication(), "answered", Toast.LENGTH_SHORT).show();

                                                                               NotificationModel notification = new NotificationModel();
                                                                               notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                               notification.setNotificationAt(new Date().getTime());
                                                                               notification.setPostId(postId);
                                                                               notification.setPostedBy(postedBy);
                                                                               notification.setType("answer");

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



                           }else {
                               reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                   @Override
                                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                       reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                           @Override
                                           public void onSuccess(Uri uri) {

                                               commentModel comment = new commentModel();
                                               comment.setAnswerimg(uri.toString());
                                               comment.setCommentedBy(FirebaseAuth.getInstance().getUid());
                                               comment.setCommentBody(binding.commentinput.getText().toString());
                                               comment.setCommentedAt(new Date().getTime());


                                               database.getReference()
                                                       .child("questions")
                                                       .child(postId)
                                                       .child("comment")
                                                       .push()
                                                       .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                           @Override
                                                           public void onSuccess(Void unused) {
                                                               database.getReference()
                                                                       .child("questions")
                                                                       .child(postId)
                                                                       .child("commentcount").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                           @Override
                                                                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                               int commentcounts = 0;
                                                                               if (snapshot.exists()) {
                                                                                   commentcounts = snapshot.getValue(Integer.class);

                                                                               }

                                                                               database.getReference()
                                                                                       .child("questions")
                                                                                       .child(postId)
                                                                                       .child("commentcount")
                                                                                       .setValue(commentcounts + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                           @Override
                                                                                           public void onSuccess(Void unused) {
                                                                                               binding.commentinput.setText("");
                                                                                               comment.setAnswerimg(null);
                                                                                               dialog.dismiss();
                                                                                               Toast.makeText(getApplication(), "answered", Toast.LENGTH_SHORT).show();

                                                                                               NotificationModel notification = new NotificationModel();
                                                                                               notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                                               notification.setNotificationAt(new Date().getTime());
                                                                                               notification.setPostId(postId);
                                                                                               notification.setPostedBy(postedBy);
                                                                                               notification.setType("answer");

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
                                   }
                               });

                           }



                       }
                   });

               }

           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });








        AnswerAdapter adapter = new AnswerAdapter(this,list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.answerRV.setLayoutManager(layoutManager);
        binding.answerRV.setAdapter(adapter);

        binding.selectanswerimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 77);
            }
        });

        database.getReference().child("questions")
                .child(postId)
                .child("comment").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            commentModel comment = dataSnapshot.getValue(commentModel.class);
                            list.add(comment);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null) {
            uri = data.getData();


        }
    }
}