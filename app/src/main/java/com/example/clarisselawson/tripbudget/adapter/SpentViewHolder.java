package com.example.clarisselawson.tripbudget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.clarisselawson.tripbudget.R;
import com.example.clarisselawson.tripbudget.Spent;

import net.steamcrafted.materialiconlib.MaterialIconView;


/**
 * Created by clarisselawson on 11/06/16.
 */
public class SpentViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private MaterialIconView imageView;
    private Spent spent;
    private TextView amount;

    public SpentViewHolder(final View itemView, final Context context) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.spent_card_text);
        amount = (TextView) itemView.findViewById(R.id.spent_card_amount);
        imageView = (MaterialIconView)itemView.findViewById(R.id.spent_card_image);
    }

    public void bind(Spent displayedSpent){
        spent = displayedSpent;
        textView.setText(displayedSpent.getLibelle() );
        amount.setText(displayedSpent.getAmount() + "€");

        imageView.setIcon(Spent.ICONS[displayedSpent.getCategory()]);
    }
}
