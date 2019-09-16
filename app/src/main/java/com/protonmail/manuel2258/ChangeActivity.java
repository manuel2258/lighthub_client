/*
 * Date: 7/30/19 3:25 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.protonmail.manuel2258.activitys.change.dates.DateRange;
import com.protonmail.manuel2258.activitys.change.dates.DateTime;
import com.protonmail.manuel2258.activitys.change.dates.RangeContainer;
import com.protonmail.manuel2258.activitys.change.time_list.TimeListAdapter;
import com.protonmail.manuel2258.networking.RequestHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import top.defaults.colorpicker.ColorPickerPopup;

public class ChangeActivity extends AppCompatActivity {

    private int currentColor;

    private String currentAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);

        currentAddress = "http://" + getIntent().getStringExtra("address");
        try {
            currentColor = RequestHandler.getInstance().getColor(currentAddress);
        } catch (IOException ioException) {
            Toast.makeText(getBaseContext(), "Error while getting initial color, " +
                            "please try again!", Toast.LENGTH_LONG).show();
        }

        int startMode = 0;
        try {
            startMode = RequestHandler.getInstance().getMode(currentAddress);
        } catch (IOException ioException) {
            Toast.makeText(getBaseContext(), "Error while getting initial mode, " +
                    "please try again!", Toast.LENGTH_LONG).show();
        }

        ((TextView)findViewById(R.id.address_label)).setText(currentAddress);
        CheckBox enableTimingCheckbox = findViewById(R.id.enable_timing_checkbox);
        Button changeColorButton = findViewById(R.id.change_color_button);
        Button addTimeButton = findViewById(R.id.add_timing_button);
        ListView timingsList = findViewById(R.id.time_list_view);

        setupColorButton(changeColorButton);

        setupTimingCheckbox(enableTimingCheckbox, addTimeButton, timingsList, startMode);

        final TimeListAdapter listAdapter = new TimeListAdapter(this, new ArrayList<>(), currentAddress);
        timingsList.setAdapter(listAdapter);

        addTimeButton.setOnClickListener(view -> {
            listAdapter.data.add(new RangeContainer(
                    new DateRange(new DateTime(0, 0),
                            new DateTime(1, 0)),1));
            listAdapter.notifyDataSetChanged();
        });

    }

    private void setupColorButton(Button changeColorButton) {
        changeColorButton.setBackgroundColor(currentColor);
        changeColorButton.setOnClickListener(view -> new ColorPickerPopup.Builder(this)
                .initialColor(currentColor)
                .enableBrightness(true)
                .okTitle("Apply")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(view, new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        changeColorButton.setBackgroundColor(color);
                        currentColor = color;
                        try {
                            RequestHandler.getInstance().setColor(currentAddress, currentColor);
                        } catch (IOException ioException) {
                            Toast.makeText(getBaseContext(), "Error while setting the color, " +
                                    "please try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                }));
    }

    private void setupTimingCheckbox(CheckBox enableTimingCheckbox,
                                     Button addTimeButton,
                                     ListView timingsList,
                                     int startMode)  {
        enableTimingCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            addTimeButton.setEnabled(isChecked);
            timingsList.setEnabled(isChecked);
            try {
                RequestHandler.getInstance().setMode(currentAddress, isChecked? 1: 0);
            } catch (IOException ioException) {
                Toast.makeText(getBaseContext(), "Error while setting mode, " +
                        "please try again!", Toast.LENGTH_LONG).show();
            }
        });
        enableTimingCheckbox.setChecked(startMode == 1);
    }

}
