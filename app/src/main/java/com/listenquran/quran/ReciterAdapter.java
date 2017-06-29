package com.listenquran.quran;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.listenquran.quran.data.ReciterContract;

import java.util.List;

/**
 * Created by lenovo on 6/29/2017.
 */

public class ReciterAdapter extends RecyclerView.Adapter<ReciterAdapter.ReciterViewHolder> {
    List<ReciterModel> mDataset;
    Context context;
  //  Cursor mCursor;
    final private ListItemClickListener mOnClickListener;

    public ReciterAdapter(List<ReciterModel> mDataset, Context context, ListItemClickListener listener) {
        this.mDataset = mDataset;
        this.context = context;
        this.mOnClickListener = listener;
    }

    @Override
    public ReciterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reciter_list_item, parent, false);
        return new ReciterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReciterViewHolder holder, int position) {

     /*   int idIndex = mCursor.getColumnIndex(ReciterContract.ReciterEntry._ID);
        int reciterName = mCursor.getColumnIndex(ReciterContract.ReciterEntry.COLUMN_RECITER_NAME);
        int reciterID = mCursor.getColumnIndex(ReciterContract.ReciterEntry.COLUMN_RECITER_ID);
        int reciterServer = mCursor.getColumnIndex(ReciterContract.ReciterEntry.COLUMN_RECITER_SERVER);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        int id = mCursor.getInt(idIndex);
        holder.itemView.setTag(id);*/


     /*   title = mCursor.getString(titleIndex);
        description = mCursor.getString(descriptionIndex);
        picked_hour = mCursor.getLong(timeIndex);*/

     //   holder.reciterTextView.setText(mCursor.getString(reciterName));
        holder.reciterTextView.setText(mDataset.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

  /*  public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)

        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }*/

    public class ReciterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView reciterTextView;

        public ReciterViewHolder(View itemView) {
            super(itemView);
            reciterTextView = (TextView) itemView.findViewById(R.id.rec_textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(getAdapterPosition());

        }
    }
}
