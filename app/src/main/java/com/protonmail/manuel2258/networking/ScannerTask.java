/*
 * Date: 5/3/19 1:12 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.networking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * A Async Task to scan for local devices.
 * Uses the ScannerTaskCallback interface to receive update callbacks.
 */
public class ScannerTask extends AsyncTask<Void, Integer, Collection<InetAddress>> {

    /**
     * How long the client should wait for a ping answer.
     */
    private static final int TIMEOUT = 100;

    /**
     * The to call callback interface.
     */
    private final ScannerTaskCallback scannerTaskCallback;

    /**
     * The current subnet string, missing the last block.
     */
    private final String subnetString;

    /**
     * The to return List of online lighthubs
     */
    private final List<InetAddress> onlineDeviceList = new ArrayList<>();

    private boolean active;

    /**
     * Creates a new activity bound scanner task.
     * @param invokeActivity The to bound activity.
     * @param callback The callback interface.
     */
    @SuppressLint("DefaultLocale")
    public ScannerTask(Activity invokeActivity, ScannerTaskCallback callback) {
        scannerTaskCallback = callback;
        subnetString = NetworkManager.getCurrentSubnet(invokeActivity);
        try {
            final JSONObject load = new JSONObject();
            load.put("type", "is_lighthub");
            load.put("data", new JSONObject());
        } catch (JSONException ignore) { }
        active = true;
    }

    /**
     * The main task function that runs in the background.
     * Pings addresses from the current subnet + [0-255].
     * Calls the onScanFailure Callback Function if a IOExceptions occurs.
     * @param voids Ignore Parameters.
     * @return A Collection of online devices.
     */
    @Override
    protected Collection<InetAddress> doInBackground(Void... voids) {
        for(int i = 1; i < 255; i++) {
            final String currentSubnet = subnetString + "." + i;
            try {
                final InetAddress currentAddress = InetAddress.getByName(currentSubnet);
                if (currentAddress.isReachable(TIMEOUT)) {
                    if(RequestHandler.getInstance().isLightHub("http://" + currentAddress.getHostAddress())) {
                        onlineDeviceList.add(currentAddress);
                    }
                }
            } catch (IOException ioException) {}
            finally {
                publishProgress(i);
            }
            if(!active) {
                break;
            }
        }

        publishProgress(999);
        return onlineDeviceList;
    }

    /**
     * The process update Function.
     * Invokes the onScanUpdate Callback with the current scan Value
     * @param values A value between [0-255]
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        scannerTaskCallback.onScanUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(Collection<InetAddress> result) {
        scannerTaskCallback.onScanSuccess(result);
    }

    /**
     * Cancels execution after the next iteration
     */
    public void cancelExecution() {
        active = false;
    }

}