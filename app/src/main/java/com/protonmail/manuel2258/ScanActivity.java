package com.protonmail.manuel2258;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.protonmail.manuel2258.activitys.scan.DeviceListAdapter;
import com.protonmail.manuel2258.networking.ScannerTask;
import com.protonmail.manuel2258.networking.ScannerTaskCallback;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScanActivity extends AppCompatActivity {

    private AsyncTask currentScannerTask;
    private ListView deviceList;

    private LinearLayout scanProgressLayout;
    private ProgressBar scanProgressBar;
    private TextView scanProgressText;

    // Creates a new anonymous ScannerTaskCallback class
    private ScannerTaskCallback scannerTaskCallback = new ScannerTaskCallback() {
        @Override
        public void onScanSuccess(Collection<InetAddress> result) {
            Context context = getApplicationContext();
            CharSequence text = "Found " + result.size() + " devices";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            List<String> addresses = new ArrayList<>();
            for(InetAddress address: result) {
                addresses.add(address.getHostAddress());
            }
            deviceList.setAdapter(new DeviceListAdapter(ScanActivity.this, addresses, address -> {
                final Intent resultIntent = new Intent();
                resultIntent.putExtra("address", address);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }));
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get activity views
        deviceList = findViewById(R.id.device_list);
        scanProgressBar = findViewById(R.id.scan_progress_bar);
        scanProgressText = findViewById(R.id.scan_progress_text);
        scanProgressLayout = findViewById(R.id.scan_layout);

        // Starts the scanner task
        currentScannerTask = new ScannerTask(ScanActivity.this, scannerTaskCallback).execute();
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
