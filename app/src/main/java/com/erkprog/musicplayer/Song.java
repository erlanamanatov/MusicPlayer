package com.erkprog.musicplayer;

import com.google.gson.annotations.SerializedName;

public class Song {
    @SerializedName("song")
    private String name;
    @SerializedName("url")
    private String url;
    @SerializedName("artists")
    private String artists;
    @SerializedName("cover_image")
    private String imageUrl;

    public Song(String name, String url, String artists, String imageUrl) {
        this.name = name;
        this.url = url;
        this.artists = artists;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getArtists() {
        return artists;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Song{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", artists='" + artists + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }



}
