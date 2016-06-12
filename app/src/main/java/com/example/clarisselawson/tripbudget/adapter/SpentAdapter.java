package com.example.clarisselawson.tripbudget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clarisselawson.tripbudget.R;
import com.example.clarisselawson.tripbudget.Spent;

import java.util.List;

/**
 * Created by clarisselawson on 11/06/16.
 */
public class SpentAdapter extends RecyclerView.Adapter<SpentViewHolder> {

    List<Spent> list;
    private LayoutInflater layoutInflater;


    public SpentAdapter(List<Spent> list, Context context) {
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public SpentViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View view = layoutInflater.inflate(R.layout.spentcard, viewGroup, false);
        return new SpentViewHolder(view, layoutInflater.getContext());
    }

    @Override
    public void onBindViewHolder(SpentViewHolder spentViewHolder, int position) {
        Spent spent = list.get(position);
        spentViewHolder.bind(spent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
