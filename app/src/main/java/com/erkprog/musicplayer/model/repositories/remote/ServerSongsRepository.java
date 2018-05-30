package com.erkprog.musicplayer.model.repositories.remote;

import com.erkprog.musicplayer.model.Song;
import com.erkprog.musicplayer.model.repositories.SongsRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerSongsRepository implements SongsRepository {
    @Override
    public void getSongList(final OnFinishedListener onFinishedListener) {

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
