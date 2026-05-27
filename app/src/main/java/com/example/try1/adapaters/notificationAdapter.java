package com.example.try1.adapaters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.try1.R;
import com.example.try1.answers;
import com.example.try1.comments;
import com.example.try1.databinding.NotificationSampleBinding;
import com.example.try1.models.NotificationModel;
import com.example.try1.models.stoaringdata;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.ViewHolder> {

    ArrayList<NotificationModel> list;
    Context context;

    public notificationAdapter(ArrayList<NotificationModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_sample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel model = list.get(position);
        String time = TimeAgo.using(model.getNotificationAt());
        String type = model.getType();

        holder.time.setText(time);

        // Set unread indicator
        if (model.isCheckOpen()) {
            holder.unreadDot.setVisibility(View.GONE);
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.notif_read_bg));
        } else {
            holder.unreadDot.setVisibility(View.VISIBLE);
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.notif_unread_bg));
        }

        // Set icon by type
        switch (type) {
            case "like":
                holder.typeIcon.setImageResource(R.drawable.like2);
                break;
            case "comment":
                holder.typeIcon.setImageResource(R.drawable.chat);
                break;
            case "answer":
                holder.typeIcon.setImageResource(R.drawable.chat);
                break;
            case "follow":
                holder.typeIcon.setImageResource(R.drawable.account);
                break;
            default:
                holder.typeIcon.setImageResource(R.drawable.notification);
                break;
        }

        // Load user info
        FirebaseDatabase.getInstance().getReference("user")
                .child(model.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        stoaringdata user = snapshot.getValue(stoaringdata.class);
                        if (user == null) return;

                        Picasso.get()
                                .load(user.getProfilephoto())
                                .placeholder(R.drawable.profilepic)
                                .into(holder.userImg);

                        String name = "<b>" + user.getUsername() + "</b> ";
                        switch (type) {
                            case "like":
                                holder.notifText.setText(Html.fromHtml(name + "liked your post"));
                                break;
                            case "comment":
                                holder.notifText.setText(Html.fromHtml(name + "commented on your post"));
                                break;
                            case "answer":
                                holder.notifText.setText(Html.fromHtml(name + "answered your question"));
                                break;
                            case "follow":
                                holder.notifText.setText(Html.fromHtml(name + "started following you"));
                                break;
                            default:
                                holder.notifText.setText(Html.fromHtml(name + "interacted with you"));
                                break;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // Click handler
        holder.card.setOnClickListener(v -> {
            // Mark as read
            String currentUid = FirebaseAuth.getInstance().getUid();
            FirebaseDatabase.getInstance().getReference("notifications")
                    .child(currentUid)
                    .child(model.getNotificationId())
                    .child("checkOpen")
                    .setValue(true);

            holder.unreadDot.setVisibility(View.GONE);
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.notif_read_bg));

            if (type.equals("follow")) return;

            Intent intent;
            if (type.equals("answer")) {
                intent = new Intent(context, answers.class);
            } else {
                intent = new Intent(context, comments.class);
            }
            intent.putExtra("postId", model.getPostId());
            intent.putExtra("postedBy", model.getPostedBy());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        ShapeableImageView userImg;
        ImageView typeIcon;
        TextView notifText, time;
        View unreadDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = (CardView) itemView;
            userImg = itemView.findViewById(R.id.userProfileImg);
            typeIcon = itemView.findViewById(R.id.notifTypeIcon);
            unreadDot = itemView.findViewById(R.id.unreadDot);
            notifText = itemView.findViewById(R.id.notificationText);
            time = itemView.findViewById(R.id.time);
        }
    }
}
