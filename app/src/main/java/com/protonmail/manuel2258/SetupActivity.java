/*
 * Date: 7/28/19 2:30 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.protonmail.manuel2258.networking.NetworkManager;
import com.protonmail.manuel2258.networking.RequestHandler;

import java.io.IOException;
import java.util.List;

public class SetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Spinner ssidInput = findViewById(R.id.ssid_spinner);
        TextView passwordInput = findViewById(R.id.password_input);

        final List<String> ssids = NetworkManager.getSavedWifiConfigurations(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, ssids);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ssidInput.setAdapter(adapter);

        Button setupButton = findViewById(R.id.send_button);
        setupButton.setOnClickListener(view -> {
            final String currentHost = "http://" + NetworkManager.getCurrentSubnet(SetupActivity.this) + ".1";
            try {
                final boolean success = RequestHandler.getInstance().setupDevice(currentHost,
                        ssidInput.getSelectedItem().toString(), passwordInput.getText().toString());
                if(success) {
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }
            } catch (IOException ioException) {
                Toast.makeText(getBaseContext(), "Error while sending request, " +
                                "please try again!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
