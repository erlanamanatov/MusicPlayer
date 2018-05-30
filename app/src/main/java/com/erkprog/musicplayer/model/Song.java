package com.erkprog.musicplayer.model;

import com.google.gson.annotations.SerializedName;

public class Song {
    @SerializedName("song")
    private String name;
    @SerializedName("url")
    private String songSource;
    @SerializedName("artists")
    private String artists;
    @SerializedName("cover_image")
    private String imageSource;

    public Song(String name, String songSource, String artists, String imageUrl) {
        this.name = name;
        this.songSource = songSource;
        this.artists = artists;
        this.imageSource = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getSongSource() {
        return songSource;
    }

    public String getArtists() {
        return artists;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSongSource(String songSource) {
        this.songSource = songSource;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    @Override
    public String toString() {
        return "Song{" +
                "name='" + name + '\'' +
                ", songSource='" + songSource + '\'' +
                ", artists='" + artists + '\'' +
                ", imageSource='" + imageSource + '\'' +
                '}';
    }
}
