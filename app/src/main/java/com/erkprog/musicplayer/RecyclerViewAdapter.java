package com.erkprog.musicplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.erkprog.musicplayer.model.Song;
import com.erkprog.musicplayer.model.SongItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SongViewHolder> {

    private List<SongItem> mSongItems;
    private Context mContext;
    private OnItemClickListener mlistener;

    public RecyclerViewAdapter(Context context, List<SongItem> songItemList) {
        mSongItems = songItemList;
        mContext = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDownloadClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public SongItem getSongItem(int position) {
        return mSongItems.get(position);
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view, mlistener);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        SongItem songItem = mSongItems.get(position);
        Song song = songItem.getSong();

        Picasso.get().load(song.getImageUrl())
                .error(R.drawable.place_holder)
                .placeholder(R.drawable.place_holder)
                .into(holder.coverImage);

        holder.name.setText(song.getName());
        holder.artists.setText(song.getArtists());


        if (songItem.isLocallyAvailable()){
            holder.downloadImg.setImageResource(R.drawable.saved);
            holder.downloadImg.setEnabled(false);
            holder.progressBar.setProgress(0);
        } else {
            holder.downloadImg.setImageResource(R.drawable.song_download);
            holder.downloadImg.setEnabled(true);
            holder.progressBar.setProgress(songItem.getProgress());
        }

    }

    @Override
    public int getItemCount() {
        return (mSongItems != null ? mSongItems.size() : 0);
    }

    void loadNewData(List<SongItem> songItemList) {
        mSongItems = songItemList;
        notifyDataSetChanged();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage, downloadImg;
        TextView name, artists;
        ProgressBar progressBar;

        public SongViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            this.coverImage = itemView.findViewById(R.id.cover_img);
            this.downloadImg = itemView.findViewById(R.id.download_img);
            this.name = itemView.findViewById(R.id.name);
            this.artists = itemView.findViewById(R.id.artists);
            this.progressBar = itemView.findViewById(R.id.songDownloadProgress);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            downloadImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDownloadClick(position);
                        }
                    }
                }
            });

        }
    }
}
