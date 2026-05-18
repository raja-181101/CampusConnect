package com.example.try1.adapaters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.try1.R;
import com.example.try1.databinding.CommentsampleBinding;
import com.example.try1.imgshow;
import com.example.try1.models.commentModel;
import com.example.try1.models.stoaringdata;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {

    Context context;
    ArrayList<commentModel> list;

    public AnswerAdapter(Context context, ArrayList<commentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AnswerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.commentsample,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerAdapter.ViewHolder holder, int position) {

        commentModel comment = list.get(position);
        String time  = TimeAgo.using(comment.getCommentedAt());
        holder.binding.time.setText(time);

        FirebaseDatabase.getInstance().getReference()
                .child("user")
                .child(comment.getCommentedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        stoaringdata user = snapshot.getValue(stoaringdata.class);
                        Picasso.get()
                                .load(user.getProfilephoto())
                                .placeholder(R.drawable.profilepic)
                                .into(holder.binding.userProfileImg);
                        holder.binding.nameUser.setText(Html.fromHtml("<b>"+user.getUsername()+"</b>" + " : "+ comment.getCommentBody()));
                        String url = comment.getAnswerimg();
                        if (url == null){
                            holder.binding.answerimage.setVisibility(View.GONE);
                        }else {


                            Picasso.get()
                                    .load(comment.getAnswerimg())
                                    .placeholder(R.drawable.profilepic)
                                    .into(holder.binding.answerimg);
                            holder.binding.answerimage.setVisibility(View.VISIBLE);
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, imgshow.class);
                                    intent.putExtra("imgurl", comment.getAnswerimg());
                                    context.startActivity(intent);
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
        CommentsampleBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CommentsampleBinding.bind(itemView);
        }
    }
}
