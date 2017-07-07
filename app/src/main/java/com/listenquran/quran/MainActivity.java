package com.listenquran.quran;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.listenquran.quran.data.ReciterContract;
import com.listenquran.quran.data.ReciterDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    TextView textView;
    Button button;
    static List<ReciterModel> mDataSet;
    private RecyclerView mRecyclerView;
    private ReciterAdapter mAdapter;
    BottomNavigationView bottomNavigationView;
    RecyclerView.LayoutManager mLayoutManager;
    static HashMap<Integer, Integer> favoriteMap;

    /**
     * Database helper that will provide us access to the database
     */
    private ReciterDbHelper mDbHelper;
    /**
     * Identifier for the reciter data loader
     */
    private static final int RECITER_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        com.facebook.stetho.Stetho.initializeWithDefaults(getApplicationContext());
        favoriteMap = new HashMap<>();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.category_home) {
                            Toast.makeText(MainActivity.this, "home", Toast.LENGTH_SHORT).show();
                        }
                        if (id == R.id.category_favorite) {
                            Toast.makeText(MainActivity.this, "favorite", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, Favorite.class));
                        }
                        if (id == R.id.category_downloads) {
                            Toast.makeText(MainActivity.this, "downloads", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });


        mDataSet = new ArrayList<>();
        mDbHelper = new ReciterDbHelper(this);


        initDataSet();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReciterAdapter(mDataSet, this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.cutom_divider));
        mRecyclerView.addItemDecoration(divider);

        textView = (TextView) findViewById(R.id.textview);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initDataSet();

            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(RECITER_LOADER, null, this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.category_home);

    }


    private void initDataSet() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   insertDate(response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("reciters");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String name = jsonObject1.getString("name");
                        String server = jsonObject1.getString("Server");
                        String id = jsonObject1.getString("id");
                        String sura = jsonObject1.getString("suras");

                        ReciterModel reciterModel = new ReciterModel(name, server, id, sura);
                        mDataSet.add(reciterModel);
                        mAdapter.notifyDataSetChanged();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Singleton.getInstance(MainActivity.this).addToReq(stringRequest);
    }


    @Override
    public void onListItemClick(int clickedItemIndex) {


        Intent intent = new Intent(MainActivity.this, Sura.class);
        String sura_number = mDataSet.get(clickedItemIndex).getSura();
        String reciter_server = mDataSet.get(clickedItemIndex).getServer();
        intent.putExtra("sura_number", sura_number);
        intent.putExtra("reciter_server", reciter_server);
        Toast.makeText(MainActivity.this, reciter_server, Toast.LENGTH_LONG).show();
        startActivity(intent);
    }


    private void insertDate(String name) {

        ContentValues values = new ContentValues();

        values.put(ReciterContract.ReciterEntry.COLUMN_RECITER_NAME, name);
        values.put(ReciterContract.ReciterEntry.COLUMN_SERVER, "mohamed");
        values.put(ReciterContract.ReciterEntry.COLUMN_SURAS, "ahmed");
        values.put(ReciterContract.ReciterEntry.COLUMN_RECITER_LETTER, "M");

        Uri uri = getContentResolver().insert(ReciterContract.ReciterEntry.CONTENT_URI, values);


    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ReciterContract.FavoriteEntry.CONTENT_URI,   // Provider content URI to query
                null,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        if (cursor.getCount() >= 1) {
            while (cursor.moveToNext()) {

                int jsonIndex = cursor.getColumnIndex(ReciterContract.FavoriteEntry.COLUMN_JSON_STRING);
                String savedJson = cursor.getString(jsonIndex);
                int savedReciterId = Integer.valueOf(savedJson);

                favoriteMap.put(savedReciterId, savedReciterId);
                mAdapter.getHash(favoriteMap);
            }

        } else {
            mAdapter.getHash(favoriteMap);
            Toast.makeText(MainActivity.this, "fail" + cursor.getCount(), Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);

    }
}
