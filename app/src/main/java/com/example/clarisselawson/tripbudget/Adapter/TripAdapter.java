package com.example.clarisselawson.tripbudget.adapter;

import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clarisselawson.tripbudget.R;
import com.example.clarisselawson.tripbudget.Trip;

import java.util.List;

/**
 * Created by clarisselawson on 11/06/16.
 */
public class TripAdapter extends RecyclerView.Adapter<TripViewHolder> {

    List<Trip> list;
    private LayoutInflater layoutInflater;


    public TripAdapter(List<Trip> list, Context context) {
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    //cette fonction permet de créer les viewHolder
//et par la même indiquer la vue à inflater (à partir des layout xml)
    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View view = layoutInflater.inflate(R.layout.tripcard, viewGroup, false);
        return new TripViewHolder(view, layoutInflater.getContext());
    }

    //c'est ici que nous allons remplir notre cellule avec le texte/image de chaque MyObjects
    @Override
    public void onBindViewHolder(TripViewHolder tripViewHolder, int position) {
        Trip trip = list.get(position);
        tripViewHolder.bind(trip);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
