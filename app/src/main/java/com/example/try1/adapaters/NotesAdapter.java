package com.example.try1.adapaters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.try1.R;
import com.example.try1.models.SubjectModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.example.try1.models.stoaringdata;
import com.squareup.picasso.Picasso;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    Context context;
    ArrayList<SubjectModel> list;

    public NotesAdapter(Context context, ArrayList<SubjectModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notes_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubjectModel model = list.get(position);
        String name = model.getDisplayName();
        holder.displayName.setText(name);

        // Set upload time if available
        if (model.getUploadedAt() > 0) {
            holder.uploadTime.setText(TimeAgo.using(model.getUploadedAt()));
        } else {
            holder.uploadTime.setText("");
        }

        // Load uploader name
        if (model.getUploadedBy() != null && !model.getUploadedBy().isEmpty()) {
            FirebaseDatabase.getInstance().getReference("user").child(model.getUploadedBy())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            stoaringdata user = snapshot.getValue(stoaringdata.class);
                            if (user != null) {
                                holder.uploaderName.setText("by " + user.getUsername());
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }

        // Determine file format
        String format = "";
        int lastDot = name.lastIndexOf(".");
        if (lastDot >= 0) format = name.substring(lastDot).toLowerCase();

        // Set icon and background tint
        switch (format) {
            case ".pdf":
                holder.notessym.setImageResource(R.drawable.pdfnotes);
                holder.notessym.setColorFilter(context.getResources().getColor(R.color.notes_pdf));
                break;
            case ".docx":
            case ".doc":
                holder.notessym.setImageResource(R.drawable.docnotes);
                holder.notessym.setColorFilter(context.getResources().getColor(R.color.notes_doc));
                break;
            case ".jpg":
            case ".jpeg":
            case ".png":
                holder.notessym.setImageResource(R.drawable.imgnotes);
                holder.notessym.setColorFilter(context.getResources().getColor(R.color.notes_img));
                break;
            case ".pptx":
            case ".ppt":
                holder.notessym.setImageResource(R.drawable.pptnotes);
                holder.notessym.setColorFilter(context.getResources().getColor(R.color.notes_ppt));
                break;
            case ".mp4":
            case ".mkv":
                holder.notessym.setImageResource(R.drawable.videonotes);
                holder.notessym.setColorFilter(context.getResources().getColor(R.color.notes_video));
                break;
            default:
                holder.notessym.setImageResource(R.drawable.othernotes);
                holder.notessym.setColorFilter(context.getResources().getColor(R.color.notes_other));
                break;
        }

        // Click on file name → open/view
        holder.displayName.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getNotesid()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "No app to open this file", Toast.LENGTH_SHORT).show();
            }
        });

        // Download button
        holder.downloadBtn.setOnClickListener(v -> {
            holder.downloadProgress.setVisibility(View.VISIBLE);
            holder.downloadBtn.setVisibility(View.GONE);

            try {
                DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(model.getNotesid());
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle(name);
                request.setDescription("Downloading via CampusConnect");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
                request.allowScanningByMediaScanner();
                dm.enqueue(request);
                Toast.makeText(context, "Downloading " + name, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            holder.downloadProgress.postDelayed(() -> {
                holder.downloadProgress.setVisibility(View.GONE);
                holder.downloadBtn.setVisibility(View.VISIBLE);
            }, 2000);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView notessym, downloadBtn;
        TextView displayName, uploaderName, uploadTime;
        ProgressBar downloadProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notessym = itemView.findViewById(R.id.notessym);
            downloadBtn = itemView.findViewById(R.id.downloadBtn);
            displayName = itemView.findViewById(R.id.displayName);
            uploaderName = itemView.findViewById(R.id.uploaderName);
            uploadTime = itemView.findViewById(R.id.uploadTime);
            downloadProgress = itemView.findViewById(R.id.downloadProgress);
        }
    }
}
