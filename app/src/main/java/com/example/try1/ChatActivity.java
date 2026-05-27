package com.example.try1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.try1.adapaters.ChatMessageAdapter;
import com.example.try1.models.ChatModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    RecyclerView messagesRV;
    EditText messageInput;
    FloatingActionButton sendBtn;
    ImageView backBtn;
    ShapeableImageView chatPartnerImg;
    TextView chatPartnerName, onlineStatus;
    View onlineIndicator;

    FirebaseAuth auth;
    FirebaseDatabase database;
    String chatUserId, chatUserName, chatUserPhoto;
    String currentUserId;
    String chatRoomId;

    ArrayList<ChatModel> messageList = new ArrayList<>();
    ChatMessageAdapter adapter;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatUserId = getIntent().getStringExtra("chatUserId");
        chatUserName = getIntent().getStringExtra("chatUserName");
        chatUserPhoto = getIntent().getStringExtra("chatUserPhoto");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        currentUserId = auth.getUid();

        // Build a consistent chat room ID from both user IDs
        chatRoomId = buildChatRoomId(currentUserId, chatUserId);

        messagesRV = findViewById(R.id.messagesRV);
        messageInput = ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.messageInput));
        sendBtn = findViewById(R.id.sendBtn);
        backBtn = findViewById(R.id.backBtn);
        chatPartnerImg = findViewById(R.id.chatPartnerImg);
        chatPartnerName = findViewById(R.id.chatPartnerName);
        onlineStatus = findViewById(R.id.onlineStatus);
        onlineIndicator = findViewById(R.id.onlineIndicator);

        chatPartnerName.setText(chatUserName);
        if (chatUserPhoto != null && !chatUserPhoto.isEmpty()) {
            Picasso.get().load(chatUserPhoto).placeholder(R.drawable.profilepic).into(chatPartnerImg);
        }

        adapter = new ChatMessageAdapter(this, messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        messagesRV.setLayoutManager(layoutManager);
        messagesRV.setAdapter(adapter);

        backBtn.setOnClickListener(v -> finish());

        sendBtn.setOnClickListener(v -> sendMessage());

        // Mark current user online
        database.getReference("presence").child(currentUserId).setValue(true);
        database.getReference("presence").child(currentUserId).onDisconnect().setValue(false);

        // Listen for partner's online status
        database.getReference("presence").child(chatUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean online = snapshot.getValue(Boolean.class);
                        if (online != null && online) {
                            onlineStatus.setText("Online");
                            onlineStatus.setTextColor(getResources().getColor(R.color.colorSecondary));
                            onlineIndicator.setVisibility(View.VISIBLE);
                        } else {
                            onlineStatus.setText("Offline");
                            onlineStatus.setTextColor(getResources().getColor(R.color.text_secondary));
                            onlineIndicator.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // Load messages
        database.getReference("chats").child(chatRoomId).child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ChatModel msg = ds.getValue(ChatModel.class);
                            if (msg != null) {
                                msg.setMessageId(ds.getKey());
                                messageList.add(msg);
                                // Mark as seen if received
                                if (!msg.getSenderId().equals(currentUserId) && !msg.isSeen()) {
                                    ds.getRef().child("seen").setValue(true);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if (!messageList.isEmpty()) {
                            messagesRV.smoothScrollToPosition(messageList.size() - 1);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        long timestamp = new Date().getTime();
        ChatModel message = new ChatModel(text, currentUserId, chatUserId, timestamp);

        database.getReference("chats").child(chatRoomId).child("messages")
                .push().setValue(message).addOnSuccessListener(unused -> {
                    messageInput.setText("");
                    // Update last message for both users
                    database.getReference("chatList").child(currentUserId).child(chatUserId)
                            .child("lastMessage").setValue(text);
                    database.getReference("chatList").child(currentUserId).child(chatUserId)
                            .child("lastMessageTime").setValue(timestamp);
                    database.getReference("chatList").child(chatUserId).child(currentUserId)
                            .child("lastMessage").setValue(text);
                    database.getReference("chatList").child(chatUserId).child(currentUserId)
                            .child("lastMessageTime").setValue(timestamp);
                });
    }

    private String buildChatRoomId(String uid1, String uid2) {
        if (uid1.compareTo(uid2) < 0) {
            return uid1 + "_" + uid2;
        } else {
            return uid2 + "_" + uid1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.getReference("presence").child(currentUserId).setValue(false);
    }
}
