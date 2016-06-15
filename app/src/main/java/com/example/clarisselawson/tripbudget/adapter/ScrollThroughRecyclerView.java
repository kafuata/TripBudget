package com.example.clarisselawson.tripbudget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by clarisselawson on 15/06/16.
 */
public class ScrollThroughRecyclerView extends RecyclerView {
    public ScrollThroughRecyclerView(Context context) {
        super(context);
    }

    public ScrollThroughRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollThroughRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        //true - block scrolling of child and allow scrolling for parent recycler
        return true;
    }
}