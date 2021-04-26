package com.niteshjha.search_image;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface FlickrApi {

//    @GET("?method=flickr.photos.search&api_key=37ad288835e4c64fc0cb8af3f3a1a65d&format=json&nojsoncallback=1&safe_search=1&text=Heidelberg")
//    Call<JsonObject>getImages();

//    @GET("?method=flickr.photos.")
//    Call<JsonObject>getPhotos(
//            @Query("method") String method,
//            @Query("api_key") String apiKey,
//            @Query("format") String format,
//            @Query("nojsoncallback") int nojsoncallback,
//            @Query("safe_search") int safe_search,
//            @Query("text") int text
//    );

    @GET("?method=flickr.photos.")
    Call<JsonObject>getPhotos(
            @QueryMap Map<String, String > parameters);
}

//    @GET("services/rest?method=flickr.photos.search&
//    api_key=37ad288835e4c64fc0cb8af3f3a1a65d&
//    format=json&
//    nojsoncallback=1&
//    safe_search=1&
//    text=Heidelberg")


//method =

// & api_key =

//