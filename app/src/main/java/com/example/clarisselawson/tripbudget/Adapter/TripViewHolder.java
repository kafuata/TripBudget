package com.example.clarisselawson.tripbudget.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clarisselawson.tripbudget.DisplayTripActivity;
import com.example.clarisselawson.tripbudget.R;
import com.example.clarisselawson.tripbudget.Trip;


/**
 * Created by clarisselawson on 11/06/16.
 */
public class TripViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private ImageView imageView;
    private Trip trip;

    public TripViewHolder(final View itemView, final Context context) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.card_text);
        imageView = (ImageView)itemView.findViewById(R.id.card_image);

        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(itemView.getContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context.getApplicationContext(), DisplayTripActivity.class);

                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", trip.getId());

                intent.putExtras(dataBundle);
                context.startActivity(intent);
            }
        });

    }

    public void bind(Trip displayedTrip){
        trip = displayedTrip;
        textView.setText(displayedTrip.getDestination());
        imageView.setImageResource(R.drawable.voyage);
    }
}
