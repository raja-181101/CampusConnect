package com.example.try1;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.try1.adapaters.ChatListAdapter;
import com.example.try1.fragments.searchfragment;
import com.example.try1.models.ChatListModel;
import com.example.try1.models.stoaringdata;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity {

    RecyclerView chatListRV;
    ImageView backBtn, newChatBtn;
    EditText searchChats;

    FirebaseAuth auth;
    FirebaseDatabase database;

    ArrayList<ChatListModel> chatList = new ArrayList<>();
    ArrayList<ChatListModel> filteredList = new ArrayList<>();
    ChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        chatListRV = findViewById(R.id.chatListRV);
        backBtn = findViewById(R.id.backBtn);
        newChatBtn = findViewById(R.id.newChatBtn);
        searchChats = ((com.google.android.material.textfield.TextInputEditText) findViewById(R.id.searchChats));

        adapter = new ChatListAdapter(this, filteredList);
        chatListRV.setLayoutManager(new LinearLayoutManager(this));
        chatListRV.setAdapter(adapter);

        backBtn.setOnClickListener(v -> finish());

        // New chat: open search/followers list
        newChatBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, searchfragment.class);
            startActivity(intent);
        });

        // Load chat list from Firebase
        database.getReference("chatList").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String partnerUid = ds.getKey();
                            String lastMsg = ds.child("lastMessage").getValue(String.class);
                            Long lastTime = ds.child("lastMessageTime").getValue(Long.class);

                            ChatListModel item = new ChatListModel();
                            item.setChatUserId(partnerUid);
                            item.setLastMessage(lastMsg != null ? lastMsg : "");
                            item.setLastMessageTime(lastTime != null ? lastTime : 0L);

                            // Load user info
                            database.getReference("user").child(partnerUid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot userSnap) {
                                            stoaringdata user = userSnap.getValue(stoaringdata.class);
                                            if (user != null) {
                                                item.setChatUserName(user.getUsername());
                                                item.setChatUserPhoto(user.getProfilephoto());
                                            }
                                            // Check online presence
                                            database.getReference("presence").child(partnerUid)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot presSnap) {
                                                            Boolean online = presSnap.getValue(Boolean.class);
                                                            item.setOnline(online != null && online);
                                                            chatList.add(item);
                                                            // Sort by most recent
                                                            chatList.sort((a, b) -> Long.compare(b.getLastMessageTime(), a.getLastMessageTime()));
                                                            filteredList.clear();
                                                            filteredList.addAll(chatList);
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError e) {}
                                                    });
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError e) {}
                                    });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // Search filter
        searchChats.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                filterChats(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterChats(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(chatList);
        } else {
            for (ChatListModel item : chatList) {
                if (item.getChatUserName() != null &&
                        item.getChatUserName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
