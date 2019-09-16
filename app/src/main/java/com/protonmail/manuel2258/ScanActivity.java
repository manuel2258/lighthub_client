package com.protonmail.manuel2258;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.protonmail.manuel2258.activitys.scan.DeviceListAdapter;
import com.protonmail.manuel2258.networking.RequestHandler;
import com.protonmail.manuel2258.networking.ScannerTask;
import com.protonmail.manuel2258.networking.ScannerTaskCallback;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ScanActivity extends AppCompatActivity {

    private ScannerTask currentScannerTask;
    private ListView deviceList;

    private LinearLayout scanProgressLayout;
    private ProgressBar scanProgressBar;
    private TextView scanProgressText;

    private List<String> addresses = new ArrayList<>();

    private ScannerTaskCallback scannerTaskCallback = new ScannerTaskCallback() {
        @Override
        public void onScanSuccess(Collection<InetAddress> result) {
            Context context = getApplicationContext();
            CharSequence text = "Found " + result.size() + " devices";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            SharedPreferences preferences = getApplicationContext().
                    getSharedPreferences("saved_addresses", 0);
            SharedPreferences.Editor editor = preferences.edit();
            for(InetAddress address: result) {
                addresses.add(address.getHostAddress());
                editor.putString(address.getHostAddress(), "-");
            }
            editor.apply();
            if(result.size() > 0) {
                refreshList();
            }

            scanProgressLayout.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onScanFailure() {
            Toast.makeText(ScanActivity.this.getApplicationContext(),
                    "Could not scan for connections. Make sure you have a active connection",
                    Toast.LENGTH_SHORT).show();
            currentScannerTask.cancel(true);
        }

        @Override
        public void onScanUpdate(int currentState) {
            if(currentState != 999) {
                scanProgressBar.setProgress(currentState);
                scanProgressText.setText(String.format(getResources().getString(R.string.scanning_progress_text), currentState));
            } else {
                scanProgressText.setText(getString(R.string.scan_concluding_text));
            }
        }
    };

    private void refreshList() {
        deviceList.setAdapter(new DeviceListAdapter(ScanActivity.this, addresses, address -> {
            currentScannerTask.cancelExecution();
            final Intent resultIntent = new Intent();
            resultIntent.putExtra("address", address);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Get activity views
        deviceList = findViewById(R.id.device_list);
        scanProgressBar = findViewById(R.id.scan_progress_bar);
        scanProgressText = findViewById(R.id.scan_progress_text);
        scanProgressLayout = findViewById(R.id.scan_layout);

        // Checks for saved addresses
        SharedPreferences preferences = getApplicationContext().
                getSharedPreferences("saved_addresses", 0);
        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet()) {
            try {

                String address = entry.getKey();
                final InetAddress currentAddress = InetAddress.getByName(address);
                if (currentAddress.isReachable(100)) {
                    if (RequestHandler.getInstance()
                            .isLightHub("http://" + currentAddress.getHostAddress())) {
                        String host = currentAddress.getHostAddress();
                        addresses.add(host);
                    }
                }

            } catch (IOException ignore) { }
        }

        refreshList();

        // Starts the scanner task
        currentScannerTask = new ScannerTask(ScanActivity.this, scannerTaskCallback);
        currentScannerTask.execute();
        scanProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
