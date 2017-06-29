package com.listenquran.quran;

import android.content.ContentValues;
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

public class MainActivity extends AppCompatActivity implements ListItemClickListener {

    TextView textView;
    Button button;
    private List<ReciterModel> mDataSet;
    private RecyclerView mRecyclerView;
    private ReciterAdapter mAdapter;
    private SQLiteDatabase mDb;
    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        com.facebook.stetho.Stetho.initializeWithDefaults(getApplicationContext());

        mDataSet = new ArrayList<>();
     //   contentValues = new ContentValues();
     //   ReciterDbHelper dbHelper = new ReciterDbHelper(this);
     //   mDb = dbHelper.getWritableDatabase();


        initDataSet();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    //    Cursor cursor = getAllGuests();

        mAdapter = new ReciterAdapter(mDataSet, this, this);
     //   mAdapter.swapCursor(cursor);
        mRecyclerView.setAdapter(mAdapter);



        textView = (TextView) findViewById(R.id.textview);
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
                    for (int i = 0; i < 5; i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String name = jsonObject1.getString("name");
                        String server = jsonObject1.getString("Server");
                        String id = jsonObject1.getString("id");

                    /*    contentValues.put(ReciterContract.ReciterEntry.COLUMN_RECITER_NAME,name);
                        contentValues.put(ReciterContract.ReciterEntry.COLUMN_RECITER_ID,id);
                        contentValues.put(ReciterContract.ReciterEntry.COLUMN_RECITER_SERVER,server);
                        mDb.insert(ReciterContract.ReciterEntry.TABLE_NAME,null,contentValues);*/


                        ReciterModel reciterModel = new ReciterModel(name, server, id);
                        mDataSet.add(reciterModel);
                        mAdapter.notifyDataSetChanged();


                            textView.append(name + "\n" + id + server + "\n");

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

    private Cursor getAllGuests() {
        return mDb.query(
                ReciterContract.ReciterEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
       ReciterModel reciterModel= mDataSet.get(clickedItemIndex);
        Toast.makeText(this,reciterModel.getId(),Toast.LENGTH_LONG).show();
    }
}
