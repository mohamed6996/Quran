package com.listenquran.quran;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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

import java.util.HashMap;
import java.util.List;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    Cursor mCursor;
    Context mContext;
    HashMap<Integer, Integer> hashMap = MainActivity.favoriteMap;
    List<ReciterModel> mDataset = ReciterAdapter.favoriteList;
    final private ListItemClickListener mOnClickListener;


    public FavoriteAdapter(Context context, ListItemClickListener listener) {
        this.mContext = context;
        this.mOnClickListener = listener;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reciter_list_item, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {

        holder.likeButton.setLiked(true);
        holder.reciterTextView.setText(mDataset.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return ReciterAdapter.favoriteList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
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

    public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView reciterTextView;
        LikeButton likeButton;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            reciterTextView = (TextView) itemView.findViewById(R.id.rec_textView);
            likeButton = (LikeButton) itemView.findViewById(R.id.like_button);
            likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    //   Toast.makeText(context, "liked", Toast.LENGTH_SHORT).show();
                    int position = getAdapterPosition();
                    Toast.makeText(mContext, "liked  " + position, Toast.LENGTH_SHORT).show();

                    ReciterModel reciterModel = mDataset.get(position);
                    String id = reciterModel.getId();
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(reciterModel, ReciterModel.class);

                    ContentValues values = new ContentValues();
                    values.put(ReciterContract.FavoriteEntry.COLUMN_JSON_STRING, id);
                    Uri uri = mContext.getContentResolver().insert(ReciterContract.FavoriteEntry.CONTENT_URI, values);


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
                    ReciterDbHelper helper = new ReciterDbHelper(mContext);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    int deletedId = db.delete(ReciterContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                    hashMap.remove(Integer.valueOf(id));
                    ReciterAdapter.favoriteList.remove(position);

                    Toast.makeText(mContext, "removed  " + deletedId, Toast.LENGTH_SHORT).show();

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
