package com.example.clarisselawson.tripbudget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clarisselawson.tripbudget.R;
import com.example.clarisselawson.tripbudget.Spent;


/**
 * Created by clarisselawson on 11/06/16.
 */
public class SpentViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private ImageView imageView;
    private Spent spent;

    public SpentViewHolder(final View itemView, final Context context) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.spent_card_text);
        imageView = (ImageView)itemView.findViewById(R.id.spent_card_image);
    }

    public void bind(Spent displayedSpent){
        spent = displayedSpent;
        textView.setText(displayedSpent.getLibelle() + ": " +
                displayedSpent.getAmount() + "€" +
                " (" + Spent.CATEGORIES[displayedSpent.getCategory()] + ")");
        imageView.setImageResource(R.drawable.monnaie);
    }
}
