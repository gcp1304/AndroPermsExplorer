package com.jayplabs.andropermsexplorer;

import android.view.View;

/**
 * Created by Chandra Gopalaiah on 22,April,2016.
 */
public interface ClickListener {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}