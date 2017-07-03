package com.listenquran.quran;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class SuraAdapter extends RecyclerView.Adapter<SuraAdapter.SuraViewHolder> {

    List<SuraModel> mDataSet;

    public SuraAdapter(List mDataSet) {
        this.mDataSet = mDataSet;
    }

    @Override
    public SuraViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sura_list_item, parent, false);
        return new SuraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SuraViewHolder holder, int position) {
        SuraModel suraModel =  mDataSet.get(position);
        holder.suraTextView.setText(suraModel.getSura_name());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class SuraViewHolder extends RecyclerView.ViewHolder {
        TextView suraTextView;

        public SuraViewHolder(View itemView) {
            super(itemView);
            suraTextView = (TextView) itemView.findViewById(R.id.sura_textView);
        }
    }
}
