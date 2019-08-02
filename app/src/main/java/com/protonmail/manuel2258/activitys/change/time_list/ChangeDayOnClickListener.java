/*
 * Date: 8/2/19 11:25 AM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.activitys.change.time_list;

import android.graphics.Color;
import android.view.View;

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
        if(currentState) {
            view.setBackgroundColor(Color.BLUE);
            container.addDay(day);
        } else {
            view.setBackgroundColor(Color.WHITE);
            container.removeDay(day);
        }
    }
}
