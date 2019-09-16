/*
 * Date: 5/5/19 2:09 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.protonmail.manuel2258.networking.NetworkManager;
import com.protonmail.manuel2258.networking.RequestHandler;

import java.io.IOException;
import java.util.Objects;

public class StartActivity extends AppCompatActivity {

    private static final int ADD_NEW_DEVICE_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_start);

        Button setupButton = findViewById(R.id.setup_button);
        setupButton.setOnClickListener(view -> {
            final String currentHost = "http://" + NetworkManager.getCurrentSubnet(this) + ".1";
            try {
                if (RequestHandler.getInstance().isLightHub(currentHost)) {
                    startActivity(new Intent(this, SetupActivity.class));
                } else {
                    Toast.makeText(getBaseContext(), currentHost + " is not a lighthub",
                            Toast.LENGTH_LONG).show();
                }
            } catch (IOException ioException) {
                Toast.makeText(getBaseContext(), "Error while sending request, " +
                                "please try again!",
                        Toast.LENGTH_LONG).show();
            }
        });

        Button scanButton = findViewById(R.id.change_button);
        scanButton.setOnClickListener(view -> {
            final Intent intent = new Intent(StartActivity.this, ScanActivity.class);
            startActivityForResult(intent, 0);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case ADD_NEW_DEVICE_REQUEST:
                final String address = Objects.requireNonNull(data).getStringExtra("address");
                final Intent intent = new Intent(this, ChangeActivity.class);
                intent.putExtra("address", address);
                startActivity(intent);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
