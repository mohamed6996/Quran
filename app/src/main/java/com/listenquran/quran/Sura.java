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

public class Sura extends AppCompatActivity implements ListItemClickListener{
    RecyclerView recyclerView;
    SuraAdapter mAdapter;

    private List<SuraModel> mDataSet;
    static List<Integer> mDataSet_sura_number;
    String[] sura_name;
    String sura_number;
    String reciter_server;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sura);
        mDataSet = new ArrayList<>();
        mDataSet_sura_number = new ArrayList<>();
        sura_name = this.getResources().getStringArray(R.array.sura_name);
        sura_number = getIntent().getStringExtra("sura_number");
        reciter_server = getIntent().getStringExtra("reciter_server");

        initDataSet();
        recyclerView = (RecyclerView) findViewById(R.id.sura_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SuraAdapter(mDataSet,this,this);
        recyclerView.setAdapter(mAdapter);


    }

    public void initDataSet() {

        StringTokenizer st = new StringTokenizer(sura_number, ",");
        while (st.hasMoreTokens()) {
            int number = Integer.valueOf(st.nextToken());
            SuraModel suraModel = new SuraModel(sura_name[number - 1]);
            mDataSet.add(suraModel);
            mDataSet_sura_number.add(number);
        }

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(Sura.this, Tset.class);
        int aya_number = mDataSet_sura_number.get(clickedItemIndex);
        intent.putExtra("reciter_server", reciter_server);
        intent.putExtra("aya_number", aya_number);
    //   System.out.println(000+mDataSet_sura_number.get(clickedItemIndex));
    //    Toast.makeText(Sura.this,""+ mDataSet_sura_number.indexOf(aya_number),Toast.LENGTH_LONG).show();

        startActivity(intent);
    }
}
