package com.example.try1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class imgshow extends AppCompatActivity {
    String imgurl;
    ImageView imgshw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgshow);
        imgshw = findViewById(R.id.imageshow);

        imgurl = getIntent().getStringExtra("imgurl");
        Picasso.get()
                .load(imgurl)
                .placeholder(R.drawable.profilepic)
                .into(imgshw);
    }
}