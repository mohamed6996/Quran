package com.listenquran.quran;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.listenquran.quran.data.ReciterContract;
import com.listenquran.quran.data.ReciterDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo on 6/29/2017.
 */

public class ReciterAdapter extends RecyclerView.Adapter<ReciterAdapter.ReciterViewHolder> {
    List<ReciterModel> mDataset;
    HashMap<Integer, Integer> hashMap;
    Context context;
    Cursor mCursor;
    final private ListItemClickListener mOnClickListener;
    static List<ReciterModel> favoriteList = new ArrayList<>();

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

        holder.reciterTextView.setText(mDataset.get(position).getName());

        if (hashMap.containsValue(Integer.valueOf(mDataset.get(position).getId()))) {
            holder.likeButton.setLiked(true);
        }
        else {
            holder.likeButton.setLiked(false);
        }


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Cursor swapCursor(Cursor c) {
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
    }


    public void getHash(HashMap<Integer, Integer> hashMap) {
        this.hashMap = hashMap;
    }

    public class ReciterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView reciterTextView;
        LikeButton likeButton;

        public ReciterViewHolder(View itemView) {
            super(itemView);
            reciterTextView = (TextView) itemView.findViewById(R.id.rec_textView);
            likeButton = (LikeButton) itemView.findViewById(R.id.like_button);
            likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    //   Toast.makeText(context, "liked", Toast.LENGTH_SHORT).show();
                    int position = getAdapterPosition();
                    Toast.makeText(context, "liked  " + position, Toast.LENGTH_SHORT).show();

                    ReciterModel reciterModel = mDataset.get(position);
                    String id = reciterModel.getId();
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(reciterModel, ReciterModel.class);

                    ContentValues values = new ContentValues();
                    values.put(ReciterContract.FavoriteEntry.COLUMN_JSON_STRING, id);
                    Uri uri = context.getContentResolver().insert(ReciterContract.FavoriteEntry.CONTENT_URI, values);

                    favoriteList.add(reciterModel);


                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    int position = getAdapterPosition();
                    ReciterModel model = mDataset.get(position);
                    String id = model.getId();
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(model, ReciterModel.class);
                    String selection = ReciterContract.FavoriteEntry.COLUMN_JSON_STRING + " = ?";
                    String[] selectionArgs = {id};
                    ReciterDbHelper helper = new ReciterDbHelper(context);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    int deletedId = db.delete(ReciterContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                    hashMap.remove(Integer.valueOf(id));

                    favoriteList.remove(position);


                    Toast.makeText(context, "removed  " + deletedId, Toast.LENGTH_SHORT).show();

                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mOnClickListener.onListItemClick(position);


        }


    }
}
