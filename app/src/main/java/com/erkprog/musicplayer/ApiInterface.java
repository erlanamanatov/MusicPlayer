package com.erkprog.musicplayer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("studio")
    Call<List<Song>> getSongs();
}
