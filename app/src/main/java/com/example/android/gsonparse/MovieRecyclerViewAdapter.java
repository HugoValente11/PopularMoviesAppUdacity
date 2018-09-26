package com.example.android.gsonparse;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.gsonparse.database.DatabaseContract;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Hugo Valente on 08/03/2018.
 */


// Click listener
    //http://www.codexpedia.com/android/defining-item-click-listener-for-recyclerview-in-android/

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter <MovieRecyclerViewAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    private String baseURLImagew185="http://image.tmdb.org/t/p/w780";
    private MyClickListener mListener;
    private Cursor mCursor;

        // data is passed into the constructor
    MovieRecyclerViewAdapter(Context context,Cursor cursor) {
        mInflater = LayoutInflater.from(context);
        this.mCursor = cursor;
        mContext = context;

    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_result, parent, false);
        return new ViewHolder(view);
    }

    public void swapCursor(Cursor newCursor) {
        // COMPLETED (16) Inside, check if the current cursor is not null, and close it if so
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        // COMPLETED (17) Update the local mCursor to be equal to  newCursor
        mCursor = newCursor;
        // COMPLETED (18) Check if the newCursor is not null, and call this.notifyDataSetChanged() if so
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }


    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String title = mCursor.getString(mCursor.getColumnIndex(DatabaseContract.MovieTable.TITLE));
        holder.myTextView.setText(title);

        String posterPath = mCursor.getString(mCursor.getColumnIndex(DatabaseContract.MovieTable.IMAGELINK));
        String imageLink;
        if (!posterPath.startsWith("http://image.tmdb.org/t/p/w780")) {
            imageLink = baseURLImagew185 + posterPath;
    }
        else {
            imageLink = posterPath;
        }

        // Bind image

        //Picasso Call to load Movie Image
        // https://www.101apps.co.za/index.php/articles/android-recyclerview-and-picasso-tutorial.html
        Picasso.get()
                .load(imageLink)
                .error(R.mipmap.errorpicasso)
//                .resize(200,200)
//                .centerCrop()
                .into(holder.myImageView);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.textViewTitle);
            myImageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
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
        void onItemClicked(Movie itemClicked);
    }



}


