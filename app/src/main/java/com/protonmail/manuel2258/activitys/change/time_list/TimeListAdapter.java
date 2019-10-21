/*
 * Date: 8/2/19 11:25 AM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

/*
 * Date: 8/1/19 3:32 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

/*
 * Date: 5/3/19 2:09 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.activitys.change.time_list;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.protonmail.manuel2258.R;
import com.protonmail.manuel2258.activitys.change.dates.RangeContainer;
import com.protonmail.manuel2258.networking.AsyncRequestCallback;
import com.protonmail.manuel2258.networking.RequestHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TimeListAdapter extends BaseAdapter {

    public AsyncRequestCallback onNewDataCallback = new AsyncRequestCallback() {
        @Override
        public void onNewData(List<RangeContainer> containers) {
            data = containers;
            context.runOnUiThread(() -> notifyDataSetChanged());
            context.runOnUiThread(() -> Toast.makeText(context,
                    "Data received!",
                    Toast.LENGTH_LONG).show());
        }

        @Override
        public void onException() {
            context.runOnUiThread(() -> Toast.makeText(context,
                    "There was a problem with the connection, please try again!",
                    Toast.LENGTH_LONG).show())
            ;
        }
    };

    private static LayoutInflater inflater = null;

    private final Activity context;
    private final String address;

    public List<RangeContainer> data;
    private List<RangeContainer> originalData;

    public TimeListAdapter(Activity invokeActivity, List<RangeContainer> data, String address) {
        this.data = data;
        originalData = new ArrayList<>();
        for(RangeContainer container: data) {
            originalData.add(new RangeContainer(container));
        }
        inflater = (LayoutInflater) invokeActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.address = address;
        this.context = invokeActivity;
        RequestHandler.getInstance().getTimes(address, onNewDataCallback);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.time_list_row, null);
        }
        final RangeContainer container = data.get(position);
        final RangeContainer originalContainer = originalData.get(position);

        final TextView fromInput = convertView.findViewById(R.id.time_from_input);
        final TextView toInput = convertView.findViewById(R.id.time_to_input);
        final ViewGroup buttonParentView = convertView.findViewById(R.id.day_layout);
        final Button applyButton = convertView.findViewById(R.id.apply_time_button);
        final Button removeButton = convertView.findViewById(R.id.time_remove_button);

        Collection<Integer> days = container.getReadonlyDays();

        fromInput.setText(container.getStart().toString());
        toInput.setText(container.getEnd().toString());

        View.OnFocusChangeListener inputFocusCallback = (view, hasFocus) -> {
            if(!hasFocus) {
                container.setDateRangeFromString(fromInput.getText().toString(),
                        toInput.getText().toString());
            }
        };

        fromInput.setOnFocusChangeListener(inputFocusCallback);
        toInput.setOnFocusChangeListener(inputFocusCallback);

        for(int i = 0; i < buttonParentView.getChildCount(); i++) {
            final boolean initialState = days.contains(i);
            Button currentButton = (Button)buttonParentView.getChildAt(i);
            currentButton.setOnClickListener(new ChangeDayOnClickListener(currentButton, container, i, initialState));
        }

        applyButton.setOnClickListener(view -> {
            try {
                RequestHandler.getInstance().removeTime(address, originalContainer.getFullJsonString(), new AsyncRequestCallback() {
                    @Override
                    public void onNewData(List<RangeContainer> containers) { }

                    @Override
                    public void onException() {
                        Toast.makeText(context, "There was a problem with the connection, " +
                                        "please try again!",
                                Toast.LENGTH_LONG).show();
                    }
                });
                container.setDateRangeFromString(fromInput.getText().toString(),
                        toInput.getText().toString());
                RequestHandler.getInstance().addTime(address, container.getFullJsonString(), onNewDataCallback);
            } catch (IllegalStateException illegalStateException) {
                Toast.makeText(context, "You can't apply a DateRange with no days selected!",
                        Toast.LENGTH_LONG).show();
            } catch (IllegalArgumentException illegalArgumentException) {
                Toast.makeText(context, "Please use a HOUR:MINUTE Format for the time!",
                        Toast.LENGTH_LONG).show();
            }
        });

        removeButton.setOnClickListener(view -> {
            try {
                RequestHandler.getInstance().removeTime(address,
                        originalContainer.getFullJsonString(),
                        onNewDataCallback);
                notifyDataSetChanged();
            } catch (IllegalStateException illegalStateException) {
                Toast.makeText(context, "You can't apply a DateRange with no days selected!",
                        Toast.LENGTH_LONG).show();
            } catch (IllegalArgumentException illegalArgumentException) {
                Toast.makeText(context, "Please use a HOUR:MINUTE Format for the time!",
                        Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        Collections.sort(data);
        originalData.clear();
        for(RangeContainer container: data) {
            originalData.add(new RangeContainer(container));
        }
        super.notifyDataSetChanged();
    }
}
