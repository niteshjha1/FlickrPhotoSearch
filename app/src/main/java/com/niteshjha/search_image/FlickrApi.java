package com.niteshjha.search_image;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface FlickrApi {

    @GET("?method=flickr.photos.")
    Call<JsonObject>getPhotos(
            @QueryMap Map<String, String > parameters);
}
