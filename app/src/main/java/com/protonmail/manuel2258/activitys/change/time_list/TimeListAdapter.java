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
import com.protonmail.manuel2258.networking.RequestHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class TimeListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;

    private final Context context;
    private List<RangeContainer> data;
    private final String address;

    public TimeListAdapter(Context context, List<RangeContainer> data, String address) {
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.address = address;
        this.context = context;
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

        final TextView fromInput = convertView.findViewById(R.id.time_from_input);
        final TextView toInput = convertView.findViewById(R.id.time_to_input);
        final ViewGroup buttonParentView = convertView.findViewById(R.id.day_layout);
        final Button applyButton = convertView.findViewById(R.id.apply_time_button);
        final Button removeButton = convertView.findViewById(R.id.time_remove_button);

        fromInput.setText(container.getStart().toString());
        toInput.setText(container.getEnd().toString());

        Collection<Integer> days = container.getReadonlyDays();

        for(int i = 1; i <= buttonParentView.getChildCount(); i++) {
            final boolean initialState = days.contains(i);
            Button currentButton = (Button)buttonParentView.getChildAt(i-1);
            currentButton.setOnClickListener(new ChangeDayOnClickListener(currentButton, container, i, initialState));
        }

        applyButton.setOnClickListener(view -> {
            try {
                container.setDateRangeFromString(fromInput.getText().toString(),
                        toInput.getText().toString());
                data = RequestHandler.getInstance().addTime(address, container.getFullJsonString());
                notifyDataSetChanged();
            } catch (IOException ioException) {
                Toast.makeText(context, "Error while sending add request, " +
                                "please try again!",
                        Toast.LENGTH_LONG).show();
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
                container.setDateRangeFromString(fromInput.getText().toString(),
                        toInput.getText().toString());
                data = RequestHandler.getInstance().removeTime(address, container.getFullJsonString());
                notifyDataSetChanged();
            } catch (IOException ioException) {
                Toast.makeText(context, "Error while sending add request, " +
                                "please try again!",
                        Toast.LENGTH_LONG).show();
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
}
