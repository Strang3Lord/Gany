package com.idkwts.bruh;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity
{
    private ImageView imageView;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imageView = findViewById(R.id.image_viewer);
        SharedPreferences prefs = getSharedPreferences("ImageView", MODE_PRIVATE);
        imageUrl = prefs.getString("imageUri", "");

        Picasso.get().load(imageUrl).into(imageView);
    }
}