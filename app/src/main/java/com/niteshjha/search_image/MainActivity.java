package com.niteshjha.search_image;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

    private FlickrApi flickrApi;
    private TextView textViewResult;

    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;

    private PhotoAdapter mAdapter;

    private boolean mLoading = false;
    private boolean mHasMore = true;

    private static final int COLUMN_NUM = 1;

    public static final String API_KEY = "37ad288835e4c64fc0cb8af3f3a1a65d";
    private static final String METHOD_SEARCH = "flickr.photos.search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(getApplicationContext(), COLUMN_NUM);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PhotoAdapter(getApplicationContext(), new ArrayList<PhotoModel>());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItem = mLayoutManager.getItemCount();
                int lastItemPos = mLayoutManager.findLastVisibleItemPosition();
                if (mHasMore && !mLoading && totalItem - 1 != lastItemPos) {
                }
            }
        });

        Map<String, String> parameters = new HashMap<>();
        parameters.put("method", METHOD_SEARCH);
        parameters.put("api_key", API_KEY);
        parameters.put("format", "json");
        parameters.put("nojsoncallback", "1");
        parameters.put("safe_search", "1");
        parameters.put("text", "cat");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.flickr.com/services/rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        flickrApi = retrofit.create(FlickrApi.class);

        Call<JsonObject> call = flickrApi.getPhotos(parameters);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
//                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                List<PhotoModel> result = new ArrayList<>();

                JsonObject photos = response.body().getAsJsonObject("photos");

                JsonArray photoArr = photos.getAsJsonArray("photo");

                Log.e("onResponse_NITESH", String.valueOf(photoArr.size()));

                for (int i = 0; i < photoArr.size(); i++) {
                    JsonObject itemObj = photoArr.get(i).getAsJsonObject();

                    PhotoModel item = new PhotoModel(
                            itemObj.getAsJsonPrimitive("id").getAsString(),
                            itemObj.getAsJsonPrimitive("secret").getAsString(),
                            itemObj.getAsJsonPrimitive("server").getAsString(),
                            itemObj.getAsJsonPrimitive("farm").getAsString());

                    result.add(item);
                }
                mAdapter.addAll(result);
                mAdapter.notifyDataSetChanged();
//                Log.e("onResponse_loop_NITESH", String.valueOf(result.get(2).getSecret()));

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                textViewResult.setText(t.getMessage());
                Log.e("onFailure_NITESH", t.toString());
            }
        });
    }
}