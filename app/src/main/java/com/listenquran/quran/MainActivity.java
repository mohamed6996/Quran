package com.listenquran.quran;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    TextView textView;
    Button button;
    private List<ReciterModel> mDataSet;
    private RecyclerView mRecyclerView;
    private ReciterAdapter mAdapter;
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

        mDataSet = new ArrayList<>();
        mDbHelper = new ReciterDbHelper(this);


        initDataSet();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ReciterAdapter(mDataSet, this, this);
        mRecyclerView.setAdapter(mAdapter);

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

    private void initDataSet() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                insertDate(response);

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
        //   values.put(ReciterContract.ReciterEntry.COLUMN_RECITER_SERVER, ".com");

        Uri uri = getContentResolver().insert(ReciterContract.ReciterEntry.CONTENT_URI, values);


    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ReciterContract.ReciterEntry.CONTENT_URI,   // Provider content URI to query
                null,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        textView.setText("" + cursor.getCount());

        try {
            while (cursor.moveToNext()) {
                String response = cursor.getString(cursor.getColumnIndex(ReciterContract.ReciterEntry.COLUMN_RECITER_NAME));

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
        } finally {
            cursor.close();
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
