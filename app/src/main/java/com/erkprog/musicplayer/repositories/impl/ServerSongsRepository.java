package com.erkprog.musicplayer.repositories.impl;

import android.widget.Toast;

import com.erkprog.musicplayer.ApiClent;
import com.erkprog.musicplayer.ApiInterface;
import com.erkprog.musicplayer.MainActivity;
import com.erkprog.musicplayer.Song;
import com.erkprog.musicplayer.repositories.SongsRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerSongsRepository implements SongsRepository {
    @Override
    public void getDataFromServer(final OnFinishedListener onFinishedListener) {

        ApiInterface api = ApiClent.getInstance().create(ApiInterface.class);

        Call<List<Song>> call = api.getSongs();

        call.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                onFinishedListener.onFinished(response.body());

            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                onFinishedListener.onFailure(t);
            }
        });
    }
}
