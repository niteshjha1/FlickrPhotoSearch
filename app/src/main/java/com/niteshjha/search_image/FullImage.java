package com.niteshjha.search_image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class FullImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_show);

        ImageView full_image = findViewById(R.id.full_image);

        Intent intent = getIntent();
        String url = intent.getStringExtra("Image_Url");

        Log.e("Nitesh_Nitesh", url);
        Glide.with(this)
                .load(url)
                .into(full_image);
        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT);

    }



}
