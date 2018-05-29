package com.erkprog.musicplayer.model.repositories.remote;

import com.erkprog.musicplayer.model.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("studio")
    Call<List<Song>> getSongs();
}
