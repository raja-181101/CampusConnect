package com.example.try1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.try1.R;
import com.example.try1.adapaters.notificationAdapter;
import com.example.try1.models.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;

public class notificationfragment extends Fragment {
    RecyclerView notificationRv;
    LinearLayout emptyNotif;
    TextView markAllRead;
    ArrayList<NotificationModel> list;
    FirebaseDatabase database;

    public notificationfragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificationfragment, container, false);

        notificationRv = view.findViewById(R.id.NotificationRV);
        emptyNotif = view.findViewById(R.id.emptyNotif);
        markAllRead = view.findViewById(R.id.markAllRead);

        list = new ArrayList<>();
        notificationAdapter adapter = new notificationAdapter(list, getContext());
        notificationRv.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationRv.setAdapter(adapter);

        String uid = FirebaseAuth.getInstance().getUid();

        database.getReference("notifications").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            NotificationModel notif = ds.getValue(NotificationModel.class);
                            if (notif != null) {
                                notif.setNotificationId(ds.getKey());
                                list.add(0, notif); // newest first
                            }
                        }
                        adapter.notifyDataSetChanged();
                        emptyNotif.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                        notificationRv.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // Mark all as read
        markAllRead.setOnClickListener(v -> {
            database.getReference("notifications").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ds.getRef().child("checkOpen").setValue(true);
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
        });

        return view;
    }
}
