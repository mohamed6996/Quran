package com.listenquran.quran;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import static com.listenquran.quran.MainActivity.mDataSet;

public class Favorite extends AppCompatActivity implements ListItemClickListener {
    private RecyclerView mRecyclerView;
    private FavoriteAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        mRecyclerView = (RecyclerView) findViewById(R.id.favorite_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FavoriteAdapter(this,this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.cutom_divider));
        mRecyclerView.addItemDecoration(divider);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {


        Intent intent = new Intent(Favorite.this, Sura.class);
        String sura_number = mDataSet.get(clickedItemIndex).getSura();
        String reciter_server = mDataSet.get(clickedItemIndex).getServer();
        intent.putExtra("sura_number", sura_number);
        intent.putExtra("reciter_server", reciter_server);
        Toast.makeText(Favorite.this, reciter_server, Toast.LENGTH_LONG).show();
        startActivity(intent);
    }
}
