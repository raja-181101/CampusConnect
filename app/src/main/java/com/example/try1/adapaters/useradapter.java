package com.example.try1.adapaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.try1.ChatActivity;
import com.example.try1.R;
import com.example.try1.databinding.UserSampleBinding;
import com.example.try1.models.NotificationModel;
import com.example.try1.models.stoaringdata;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_sample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        stoaringdata model = list.get(position);

        // nameUser is the ID used in user_sample.xml (matches FollowAdapter too)
        holder.binding.nameUser.setText(model.getUsername());
        holder.binding.proffision.setText(model.getProffision() != null ? model.getProffision() : "");

        if (model.getProfilephoto() != null && !model.getProfilephoto().isEmpty()) {
            Picasso.get().load(model.getProfilephoto())
                    .placeholder(R.drawable.profilepic)
                    .into(holder.binding.userProfileImg);
        }

        String myUid = FirebaseAuth.getInstance().getUid();

        // Check follow state
        FirebaseDatabase.getInstance().getReference("followers")
                .child(model.getUserid()).child(myUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.binding.Follow.setText("Following");
                            holder.binding.Follow.setAlpha(0.55f);
                        } else {
                            holder.binding.Follow.setText("Follow");
                            holder.binding.Follow.setAlpha(1.0f);
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });

        holder.binding.Follow.setOnClickListener(v -> {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            db.getReference("followers").child(model.getUserid()).child(myUid).setValue(true);
            db.getReference("following").child(myUid).child(model.getUserid()).setValue(true);
            holder.binding.Follow.setText("Following");
            holder.binding.Follow.setAlpha(0.55f);

            NotificationModel notif = new NotificationModel();
            notif.setNotificationBy(myUid);
            notif.setNotificationAt(new Date().getTime());
            notif.setType("follow");
            notif.setPostedBy(model.getUserid());
            db.getReference("notifications").child(model.getUserid()).push().setValue(notif);
        });

        holder.binding.chatBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("chatUserId", model.getUserid());
            intent.putExtra("chatUserName", model.getUsername());
            intent.putExtra("chatUserPhoto", model.getProfilephoto());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        UserSampleBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = UserSampleBinding.bind(itemView);
        }
    }
}
