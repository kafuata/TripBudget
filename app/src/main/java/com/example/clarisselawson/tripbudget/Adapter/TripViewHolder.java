package com.example.clarisselawson.tripbudget.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clarisselawson.tripbudget.DisplayTripActivity;
import com.example.clarisselawson.tripbudget.R;
import com.example.clarisselawson.tripbudget.Trip;

import java.io.FileInputStream;


/**
 * Created by clarisselawson on 11/06/16.
 */
public class TripViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private ImageView imageView;
    private TextView textView2;
    private TextView textView3;
    private Trip trip;
    private Context context;

    public TripViewHolder(final View itemView, final Context context) {
        super(itemView);
        this.context = context;

        textView = (TextView) itemView.findViewById(R.id.trip_card_text);
        textView2 = (TextView) itemView.findViewById(R.id.trip_card_budget);
        textView3 = (TextView) itemView.findViewById(R.id.trip_card_start_and_finish);
        imageView = (ImageView)itemView.findViewById(R.id.trip_card_image);

        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(itemView.getContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context.getApplicationContext(), DisplayTripActivity.class);
                intent.putExtra("trip", trip);
                context.startActivity(intent);
            }
        });
    }

    public void bind(Trip displayedTrip){
        trip = displayedTrip;

        textView.setText(displayedTrip.getDestination());

        textView2.setText(displayedTrip.getBudget().toString()+ "€");

        String start = DateFormat.format("dd-MM", displayedTrip.getStartDate()).toString();
        String finish = DateFormat.format("dd-MM", displayedTrip.getFinishDate()).toString();
        textView3.setText(start + " au " + finish);

        try {
            FileInputStream input = context.openFileInput(String.valueOf(trip.getId()));
            Bitmap bitmap = BitmapFactory.decodeStream(input);

            imageView.setImageBitmap(bitmap);
            input.close();
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.voyage);
        }
    }

    public ImageView getImageView() {
        return imageView;
    }
}
