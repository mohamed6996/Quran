package com.listenquran.quran;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.listenquran.quran.data.ReciterContract;
import com.listenquran.quran.data.ReciterDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements ListItemClickListener {

    TextView textView;
    Button button;
    private List<ReciterModel> mDataSet;
    private RecyclerView mRecyclerView;
    private ReciterAdapter mAdapter;
    /**
     * Database helper that will provide us access to the database
     */
    private ReciterDbHelper mDbHelper;
    //  private SQLiteDatabase db;
    //  ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        com.facebook.stetho.Stetho.initializeWithDefaults(getApplicationContext());

        mDataSet = new ArrayList<>();
        //   contentValues = new ContentValues();
        mDbHelper = new ReciterDbHelper(this);
        //    db = mDbHelper.getWritableDatabase();


        initDataSet();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //    Cursor cursor = getAllGuests();

        mAdapter = new ReciterAdapter(mDataSet, this, this);
        //   mAdapter.swapCursor(cursor);
        mRecyclerView.setAdapter(mAdapter);


        textView = (TextView) findViewById(R.id.textview);
        insertFakeDate();
        displayDatabaseInfo();
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initDataSet();

            }
        });


    }

    private void initDataSet() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("reciters");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String name = jsonObject1.getString("name");
                        String server = jsonObject1.getString("Server");
                        String id = jsonObject1.getString("id");
                        String sura = jsonObject1.getString("suras");



                    /*    contentValues.put(ReciterContract.ReciterEntry.COLUMN_RECITER_NAME,name);
                        contentValues.put(ReciterContract.ReciterEntry.COLUMN_RECITER_ID,id);
                        contentValues.put(ReciterContract.ReciterEntry.COLUMN_RECITER_SERVER,server);
                        mDb.insert(ReciterContract.ReciterEntry.TABLE_NAME,null,contentValues);*/


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

   /* private Cursor getAllGuests() {
        return db.query(
                ReciterContract.ReciterEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }*/

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

    private void displayDatabaseInfo() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform a query on the pets table
        Cursor cursor = db.query(
                ReciterContract.ReciterEntry.TABLE_NAME,   // The table to query
                null,                  // projection,The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                 // The sort order

        textView.setText("" + cursor.getCount());
    }

    private void insertFakeDate() {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ReciterContract.ReciterEntry.COLUMN_RECITER_NAME, "Mohamed");
        values.put(ReciterContract.ReciterEntry.COLUMN_SERVER, "Mohamed");
        values.put(ReciterContract.ReciterEntry.COLUMN_SURAS, "Mohamed");
        values.put(ReciterContract.ReciterEntry.COLUMN_RECITER_LETTER, "M");
        //   values.put(ReciterContract.ReciterEntry.COLUMN_RECITER_SERVER, ".com");


        long newRowId = db.insert(ReciterContract.ReciterEntry.TABLE_NAME, null, values);
        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Pet saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }


    }

}
