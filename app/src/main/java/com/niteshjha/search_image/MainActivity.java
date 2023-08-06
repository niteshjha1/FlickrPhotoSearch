package com.niteshjha.search_image;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
        // Clear the adapter data and notify
        mAdapter.getList().clear();
        mAdapter.notifyDataSetChanged();
    }
}
