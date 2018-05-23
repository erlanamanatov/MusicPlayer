package com.erkprog.musicplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SongViewHolder> {

    private List<Song> mSongList;
    private Context mContext;

    public RecyclerViewAdapter(Context context, List<Song> songList) {
        mSongList = songList;
        mContext = context;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        Song songItem = mSongList.get(position);

        Picasso.get().load(songItem.getImageUrl())
                .error(R.drawable.place_holder)
                .placeholder(R.drawable.place_holder)
                .into(holder.coverImage);

        holder.name.setText(songItem.getName());
        holder.artists.setText(songItem.getArtists());

    }

    @Override
    public int getItemCount() {
        return (mSongList != null ? mSongList.size() : 0);
    }

    void loadNewData(List<Song> songList){
        mSongList = songList;
        notifyDataSetChanged();
    }

    static  class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage, downloadImg;
        TextView name, artists;

        public SongViewHolder(View itemView) {
            super(itemView);

            this.coverImage = itemView.findViewById(R.id.cover_img);
            this.downloadImg = itemView.findViewById(R.id.download_img);
            this.name = itemView.findViewById(R.id.name);
            this.artists = itemView.findViewById(R.id.artists);
        }
    }
}
