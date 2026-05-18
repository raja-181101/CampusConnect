package com.example.try1.adapaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.try1.R;
import com.example.try1.answers;
import com.example.try1.comments;
import com.example.try1.databinding.QuestionsSampleBinding;
import com.example.try1.imgshow;
import com.example.try1.models.PostModel;
import com.example.try1.models.stoaringdata;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class questionAdapter extends RecyclerView.Adapter<questionAdapter.ViewHolder> {


    Context context;
    ArrayList<PostModel> list;


    public questionAdapter(Context context, ArrayList<PostModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public questionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.questions_sample, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull questionAdapter.ViewHolder holder, int position) {

        PostModel model = list.get(position);
        holder.binding.like.setText(model.getPostlikes() + "");
        holder.binding.comment.setText(model.getCommentcount() + "");
        holder.binding.Questions.setText(model.getPostdiscription());

        String img = model.getPostimage();

        if (img == null) {
            holder.binding.imageView2.setVisibility(View.GONE);
        } else {
            Picasso.get()
                    .load(model.getPostimage())
                    .placeholder(R.drawable.profilepic)
                    .into(holder.binding.imageView2);
            holder.binding.imageView2.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, imgshow.class);
                    intent.putExtra("imgurl", model.getPostimage());
                    context.startActivity(intent);
                }
            });
        }

        FirebaseDatabase.getInstance().getReference().child("user")
                .child(model.getPostedby()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        stoaringdata users = snapshot.getValue(stoaringdata.class);

                        Picasso.get()
                                .load(users.getProfilephoto())
                                .placeholder(R.drawable.profilepic)
                                .into(holder.binding.userProfileImg);
                        holder.binding.textView3.setText(users.getUsername());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("questions")
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
                                            .child("questions")
                                            .child(model.getPostid())
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("questions")
                                                            .child(model.getPostid())
                                                            .child("postlikes")
                                                            .setValue(model.getPostlikes() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like2, 0, 0, 0);

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
                Intent intent = new Intent(context, answers.class);
                intent.putExtra("postId", model.getPostid());
                intent.putExtra("postedBy", model.getPostedby());
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
        QuestionsSampleBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = QuestionsSampleBinding.bind(itemView);
        }
    }
}
