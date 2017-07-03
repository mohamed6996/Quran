package com.listenquran.quran;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Sura extends AppCompatActivity {
    RecyclerView recyclerView;
    SuraAdapter mAdapter;

    private List<SuraModel> mDataSet;
    String[] sura_name;
    String sura_number;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sura);
        mDataSet = new ArrayList<>();
        sura_name = this.getResources().getStringArray(R.array.sura_name);
        sura_number = getIntent().getStringExtra("sura_number");

        initDataSet();
        recyclerView = (RecyclerView) findViewById(R.id.sura_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SuraAdapter(mDataSet);
        recyclerView.setAdapter(mAdapter);


    }

    public void initDataSet() {

        StringTokenizer st = new StringTokenizer(sura_number, ",");
        while (st.hasMoreTokens()) {
            int number = Integer.valueOf(st.nextToken());
            SuraModel suraModel = new SuraModel(sura_name[number - 1]);
            mDataSet.add(suraModel);
        }

    }
}
