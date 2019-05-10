/*
 * Date: 5/3/19 2:09 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.activitys.scan_screen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.protonmail.manuel2258.R;

import java.util.List;

public class DeviceListAdapter extends BaseAdapter {

    private List<String> data;
    private static LayoutInflater inflater = null;
    private final DeviceAddListener deviceAddListener;

    public DeviceListAdapter(Context context, List<String> data, DeviceAddListener deviceAddListener) {
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.deviceAddListener = deviceAddListener;
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
            convertView = inflater.inflate(R.layout.device_list_row, null);
        }
        final String currentAddress = data.get(position);

        final TextView text = convertView.findViewById(R.id.address);
        text.setText(currentAddress);

        final Button addButton = convertView.findViewById(R.id.add_device_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceAddListener.onClick(currentAddress);
            }
        });
        return convertView;
    }
}
