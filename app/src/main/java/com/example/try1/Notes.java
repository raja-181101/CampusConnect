package com.example.try1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.try1.adapaters.NotesAdapter;
import com.example.try1.models.SubjectModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import java.util.ArrayList;
import java.util.Date;

public class Notes extends AppCompatActivity {

    RecyclerView notesRV;
    MaterialButton uploadBtn;
    ImageView backBtn;
    TextView notesTitle;
    LinearLayout emptyState, uploadProgressLayout;
    LinearProgressIndicator uploadProgressBar;
    TextView uploadProgressText;
    ChipGroup filterChips;

    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;

    String subjectName, sec, year, branch;
    ArrayList<SubjectModel> allNotes = new ArrayList<>();
    ArrayList<SubjectModel> filteredNotes = new ArrayList<>();
    NotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        subjectName = getIntent().getStringExtra("subjectName");
        sec = getIntent().getStringExtra("sec");
        year = getIntent().getStringExtra("year");
        branch = getIntent().getStringExtra("branch");

        notesRV = findViewById(R.id.notesRV);
        uploadBtn = findViewById(R.id.uploadBtn);
        backBtn = findViewById(R.id.backBtn);
        notesTitle = findViewById(R.id.notesTitle);
        emptyState = findViewById(R.id.emptyState);
        uploadProgressLayout = findViewById(R.id.uploadProgressLayout);
        uploadProgressBar = findViewById(R.id.uploadProgressBar);
        uploadProgressText = findViewById(R.id.uploadProgressText);
        filterChips = findViewById(R.id.filterChips);

        if (subjectName != null) notesTitle.setText(subjectName);

        adapter = new NotesAdapter(this, filteredNotes);
        notesRV.setLayoutManager(new LinearLayoutManager(this));
        notesRV.setAdapter(adapter);

        backBtn.setOnClickListener(v -> finish());

        uploadBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            String[] mimeTypes = {"application/pdf", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "image/*", "video/mp4"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(intent, 100);
        });

        // Filter chips
        filterChips.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipAll) filterByType("");
            else if (checkedId == R.id.chipPdf) filterByType(".pdf");
            else if (checkedId == R.id.chipDoc) filterByType(".docx");
            else if (checkedId == R.id.chipImage) filterByType(".jpg");
            else if (checkedId == R.id.chipVideo) filterByType(".mp4");
        });

        loadNotes();
    }

    private void loadNotes() {
        String path = "notes";
        if (subjectName != null) path += "/" + subjectName;

        database.getReference(path)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allNotes.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            SubjectModel note = ds.getValue(SubjectModel.class);
                            if (note != null) allNotes.add(note);
                        }
                        // Sort by time, newest first
                        allNotes.sort((a, b) -> Long.compare(b.getUploadedAt(), a.getUploadedAt()));
                        filteredNotes.clear();
                        filteredNotes.addAll(allNotes);
                        adapter.notifyDataSetChanged();

                        emptyState.setVisibility(allNotes.isEmpty() ? View.VISIBLE : View.GONE);
                        notesRV.setVisibility(allNotes.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void filterByType(String ext) {
        filteredNotes.clear();
        if (ext.isEmpty()) {
            filteredNotes.addAll(allNotes);
        } else {
            for (SubjectModel note : allNotes) {
                String name = note.getDisplayName().toLowerCase();
                if (ext.equals(".jpg")) {
                    if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"))
                        filteredNotes.add(note);
                } else if (ext.equals(".mp4")) {
                    if (name.endsWith(".mp4") || name.endsWith(".mkv"))
                        filteredNotes.add(note);
                } else {
                    if (name.endsWith(ext)) filteredNotes.add(note);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            String fileName = getFileName(fileUri);
            uploadFile(fileUri, fileName);
        }
    }

    private void uploadFile(Uri fileUri, String fileName) {
        uploadProgressLayout.setVisibility(View.VISIBLE);
        uploadProgressText.setText("Uploading " + fileName + "...");
        uploadProgressBar.setProgress(0);

        StorageReference ref = storage.getReference()
                .child("notes")
                .child(subjectName != null ? subjectName : "general")
                .child(fileName);

        ref.putFile(fileUri)
                .addOnProgressListener(snapshot -> {
                    int progress = (int) (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    uploadProgressBar.setProgress(progress);
                    uploadProgressText.setText("Uploading... " + progress + "%");
                })
                .addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        SubjectModel note = new SubjectModel();
                        note.setDisplayName(fileName);
                        note.setNotesid(uri.toString());
                        note.setSubjectName(subjectName);
                        note.setSec(sec);
                        note.setYear(year);
                        note.setBranch(branch);
                        note.setUploadedBy(auth.getUid());
                        note.setUploadedAt(new Date().getTime());

                        String path = "notes";
                        if (subjectName != null) path += "/" + subjectName;
                        database.getReference(path).push().setValue(note);

                        uploadProgressLayout.setVisibility(View.GONE);
                        Toast.makeText(this, "Uploaded successfully!", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    uploadProgressLayout.setVisibility(View.GONE);
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (idx >= 0) result = cursor.getString(idx);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }
}
