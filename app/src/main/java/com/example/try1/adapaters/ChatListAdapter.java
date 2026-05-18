package com.example.try1.adapaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.try1.ChatActivity;
import com.example.try1.R;
import com.example.try1.models.ChatListModel;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    Context context;
    ArrayList<ChatListModel> list;

    public ChatListAdapter(Context context, ArrayList<ChatListModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatListModel model = list.get(position);

        holder.userName.setText(model.getChatUserName());
        holder.lastMsg.setText(model.getLastMessage() != null ? model.getLastMessage() : "Tap to chat");
        holder.lastTime.setText(TimeAgo.using(model.getLastMessageTime()));

        if (model.getChatUserPhoto() != null && !model.getChatUserPhoto().isEmpty()) {
            Picasso.get().load(model.getChatUserPhoto())
                    .placeholder(R.drawable.profilepic)
                    .into(holder.userImg);
        }

        if (model.isOnline()) {
            holder.onlineDot.setVisibility(View.VISIBLE);
        } else {
            holder.onlineDot.setVisibility(View.GONE);
        }

        if (model.getUnreadCount() > 0) {
            holder.unreadCount.setVisibility(View.VISIBLE);
            holder.unreadCount.setText(String.valueOf(model.getUnreadCount()));
        } else {
            holder.unreadCount.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("chatUserId", model.getChatUserId());
            intent.putExtra("chatUserName", model.getChatUserName());
            intent.putExtra("chatUserPhoto", model.getChatUserPhoto());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView userImg;
        TextView userName, lastMsg, lastTime, unreadCount;
        View onlineDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.chatUserImg);
            userName = itemView.findViewById(R.id.chatUserName);
            lastMsg = itemView.findViewById(R.id.lastMessage);
            lastTime = itemView.findViewById(R.id.lastMsgTime);
            unreadCount = itemView.findViewById(R.id.unreadCount);
            onlineDot = itemView.findViewById(R.id.onlineDot);
        }
    }
}
