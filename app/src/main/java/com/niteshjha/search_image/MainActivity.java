package com.niteshjha.search_image;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String API_KEY = "37ad288835e4c64fc0cb8af3f3a1a65d";
    private static final String METHOD_SEARCH = "flickr.photos.search";

    private EditText searchEditText;
    private Button searchButton, clear_button;
    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 123;



    // Set the number of columns for the grid layout
    private static final int COLUMN_NUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.search_text);
        searchButton = findViewById(R.id.Search_button);
        clear_button = findViewById(R.id.Clear_button);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, COLUMN_NUM));
        mAdapter = new PhotoAdapter(this, new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);

        // Set up search button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the search text from EditText
                String searchText = searchEditText.getText().toString().trim();

                if (searchText.isEmpty()) {
                    // Show a toast message if search text is empty
                    Toast.makeText(MainActivity.this, "Enter a search keyword", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform image search
                    performImageSearch(searchText);
                }
            }
        });

        // Set click listener for Clear button
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearResults();
            }
        });

        mAdapter.setOnItemClickListener(photo -> {
            showImageDialog(photo);
        });
    }

    // Method to perform image search using Retrofit and Flickr API
    private void performImageSearch(String searchText) {
        // Set up parameters for the API request
        Map<String, String> parameters = new HashMap<>();
        parameters.put("method", METHOD_SEARCH);
        parameters.put("api_key", API_KEY);
        parameters.put("format", "json");
        parameters.put("nojsoncallback", "1");
        parameters.put("safe_search", "1");
        parameters.put("text", searchText);

        // Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.flickr.com/services/rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Api interface
        FlickrApi flickrApi = retrofit.create(FlickrApi.class);

        // Create API call
        Call<JsonObject> call = flickrApi.getPhotos(parameters);

        // Enqueue the call for asynchronous execution
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    Log.e("API Response", "Code: " + response.code());
                    return;
                }

                List<PhotoModel> result = new ArrayList<>();
                JsonObject photos = response.body().getAsJsonObject("photos");

                if (photos != null) {
                    JsonArray photoArr = photos.getAsJsonArray("photo");

                    for (int i = 0; i < photoArr.size(); i++) {
                        JsonObject itemObj = photoArr.get(i).getAsJsonObject();

                        // Create PhotoModel object and add to result list
                        PhotoModel item = new PhotoModel(
                                itemObj.getAsJsonPrimitive("id").getAsString(),
                                itemObj.getAsJsonPrimitive("secret").getAsString(),
                                itemObj.getAsJsonPrimitive("server").getAsString(),
                                itemObj.getAsJsonPrimitive("farm").getAsString());

                        result.add(item);
                    }

                    mAdapter.addAll(result);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API Response", "No 'photos' object found in the response");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Handle API call failure
                Log.e("API Failure", t.toString());
            }
        });
    }

    private void clearResults() {
        mAdapter.getList().clear();
        mAdapter.notifyDataSetChanged();
    }

    private class ImageDownloadTask extends AsyncTask<Void, Void, Bitmap> {
        private WeakReference<Context> contextReference;
        private String imageUrl;

        ImageDownloadTask(Context context, String imageUrl) {
            contextReference = new WeakReference<>(context);
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap = null;
            try {
                Context context = contextReference.get();
                if (context != null) {
                    bitmap = Glide.with(context)
                            .asBitmap()
                            .load(imageUrl)
                            .submit()
                            .get();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Context context = contextReference.get();
            if (context != null && bitmap != null) {
                // Save the bitmap to the gallery on the main thread
                String displayName = "Image_" + System.currentTimeMillis() + ".jpg";
                String mimeType = "image/jpeg";
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
                values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                if (bitmap != null) {
                    AsyncTask.execute(() -> {
                        try {
                            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.close();

                            // Show toast on the main thread after saving
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> {
                                Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show();
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> {
                                Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }
            }
        }
    }

        private void showImageDialog(PhotoModel photo) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_image_view, null);
            final Context context = MainActivity.this;

            ImageView dialogImageView = dialogView.findViewById(R.id.dialog_image);
            Button saveButton = dialogView.findViewById(R.id.dialog_save_button);

            // Load and set the image to the dialog ImageView
            Glide.with(MainActivity.this)
                    .load(photo.getUrl())
                    .into(dialogImageView);

            // Click "Save" button
            saveButton.setOnClickListener(view -> {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // Create and execute the AsyncTask for image downloading
                    ImageDownloadTask downloadTask = new ImageDownloadTask(context, photo.getUrl());
                    downloadTask.execute();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
                }
            });

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
}
