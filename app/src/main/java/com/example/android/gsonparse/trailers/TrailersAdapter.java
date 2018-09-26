package com.example.android.gsonparse.trailers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.gsonparse.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Hugo Valente on 16/03/2018.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {
    private List<VideoResults> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public TrailersAdapter(Context context, List<VideoResults> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.trailers_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String movieKey = mData.get(position).getKey();
        String youtubeLink = "https://img.youtube.com/vi/" + movieKey + "/maxresdefault.jpg";

        Picasso.get()
                .load(youtubeLink)
                .error(R.drawable.imagenotfound)
                .into(holder.trailerImageView);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView trailerImageView;


        ViewHolder(View itemView) {
            super(itemView);
            trailerImageView = itemView.findViewById(R.id.trailerImageView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    VideoResults getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public interface OnItemClickListener {
        public void onClick(View view, int position);
    }


    public interface MyClickListener {
        void onItemClicked(VideoResults itemClicked);
    }

}