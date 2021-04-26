package com.niteshjha.search_image;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FlickrApi {

    @GET("?method=flickr.photos.search&api_key=37ad288835e4c64fc0cb8af3f3a1a65d&format=json&nojsoncallback=1&safe_search=1&text=Heidelberg")
    Call<JsonObject>getImages();

}
