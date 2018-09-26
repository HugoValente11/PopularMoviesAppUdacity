package com.example.android.gsonparse;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.gsonparse.database.DatabaseContract;
import com.squareup.picasso.Picasso;

public class CursorAdapter extends RecyclerView.Adapter<CursorAdapter.ViewHolder>{

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;
    private String baseURLImagew185="http://image.tmdb.org/t/p/w185";
    private CursorAdapterOnClickHandler mClickHandler;


    /**
     * The interface that receives onClick messages.
     */
    public interface CursorAdapterOnClickHandler {
        void onClick(long id);
    }


    /**
     * Constructor for the CursorAdapter that initializes the Context.
     *
     * @param mContext the current Context
     */
    public CursorAdapter(Context mContext, CursorAdapterOnClickHandler mClickHandler) {
        this.mContext = mContext;
        this.mClickHandler = mClickHandler;

    }


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new ViewHolder that holds the view for each task
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.row_result, parent, false);

        return new ViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        mCursor.moveToPosition(position);

        String title = mCursor.getString(mCursor.getColumnIndex(DatabaseContract.MovieTable.TITLE));
        holder.myTextView.setText(title);

        String posterPath = mCursor.getString(mCursor.getColumnIndex(DatabaseContract.MovieTable.IMAGELINK));
        String imageLink;
        if (!posterPath.startsWith("http://image.tmdb.org/t/p/w185")) {
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


    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
//        // check if this cursor is the same as the previous cursor (mCursor)
//        if (mCursor == c) {
//            return null; // bc nothing has changed
//        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    // Inner class for creating ViewHolders
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView myTextView;
        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.textViewTitle);
            myImageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
//          COMPLETED (37) Instead of passing the String for the clicked item, pass the date from the cursor
            mCursor.moveToPosition(adapterPosition);
            long id = mCursor.getLong(0);
            mClickHandler.onClick(id);
        }
    }


}
