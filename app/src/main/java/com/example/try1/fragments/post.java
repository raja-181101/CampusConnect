package com.example.try1.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.try1.R;
import com.example.try1.databinding.FragmentPostBinding;
import com.example.try1.models.PostModel;
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

import java.util.Date;

public class post extends Fragment {
    FragmentPostBinding binding;
    Uri uri;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    ProgressDialog dialog;


    public post() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog= new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPostBinding.inflate(inflater, container, false);
        dialog.setTitle("Uploading");
        dialog.setMessage("please wait..");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        database.getReference().child("user")
                        .child(auth.getUid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            stoaringdata users = snapshot.getValue(stoaringdata.class);
                                            Picasso.get()
                                                    .load(users.getProfilephoto())
                                                    .placeholder(R.drawable.profilepic)
                                                    .into(binding.userProfileImg);
                                            binding.nameUser.setText(users.getUsername());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


        binding.discription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String discreption = binding.discription.getText().toString();
                if (!discreption.isEmpty()) {
                    binding.postbutton.setBackgroundColor(getContext().getResources().getColor(R.color.purple_500));
                    binding.postbutton.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.postbutton.setEnabled(true);
                } else {
                    binding.postbutton.setBackgroundColor(getContext().getResources().getColor(R.color.transperant));
                    binding.postbutton.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.postbutton.setEnabled(false);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.postimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 10);

            }
        });


        binding.postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                final StorageReference reference = storage.getReference().child("posts")
                        .child(auth.getUid())
                        .child(new Date().getTime()+"");
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                      reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                          @Override
                          public void onSuccess(Uri uri) {
                          PostModel post = new PostModel();
                          post.setPostimage(uri.toString());
                          post.setPostdiscription(binding.discription.getText().toString());
                          post.setPostedby(auth.getUid());
                          post.setPostedAt(new Date().getTime());

                          database.getReference().child("posts")
                                  .push()
                                  .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void unused) {
                                          Toast.makeText(getContext(),"success",Toast.LENGTH_SHORT).show();
                                          binding.postimg69.setVisibility(View.GONE);
                                          binding.discription.setHint("Description");
                                          binding.discription.setText("");
                                          dialog.dismiss();
                                      }
                                  });

                          }
                      });
                    }
                });
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null) {
            uri = data.getData();
            binding.postimg69.setImageURI(uri);
            binding.postimg69.setVisibility(View.VISIBLE);
            binding.postbutton.setBackgroundColor(getContext().getResources().getColor(R.color.purple_500));
            binding.postbutton.setTextColor(getContext().getResources().getColor(R.color.white));
            binding.postbutton.setEnabled(true);


        }

    }
}