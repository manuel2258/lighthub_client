/*
 * Date: 8/2/19 11:25 AM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.activitys.change.time_list;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.protonmail.manuel2258.activitys.change.dates.RangeContainer;

class ChangeDayOnClickListener implements View.OnClickListener {

    private final int day;
    private final RangeContainer container;
    private boolean currentState;

    public ChangeDayOnClickListener(View view, RangeContainer container, int day, boolean initialState) {
        this.day = day;
        this.container = container;
        currentState = initialState;
        updateState(view);
    }

    public void onClick(View view) {
        currentState = !currentState;
        updateState(view);
    }

    private void updateState(View view) {
        TextView textView = (TextView)view;
        if(currentState) {
            textView.setTextColor(Color.argb(255, 230,81,0));
            container.addDay(day);
        } else {
            textView.setTextColor(Color.argb(255, 33,33,33));
            container.removeDay(day);
        }
    }
}
