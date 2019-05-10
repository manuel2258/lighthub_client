/*
 * Date: 5/3/19 1:12 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.networking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A Async Task to scan for local devices.
 * Uses the ScannerTaskCallback interface to receive update callbacks.
 */
public class ScannerTask extends AsyncTask<Void, Integer, Collection<InetAddress>> {

    /**
     * How long the client should wait for a ping answer.
     */
    private static final int TIMEOUT = 50;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

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

    OkHttpClient client = new OkHttpClient();

    private JSONObject jsonRequest;

    /**
     * Creates a new activity bound scanner task.
     * @param invokeActivity The to bound activity.
     * @param callback The callback interface.
     */
    @SuppressLint("DefaultLocale")
    public ScannerTask(Activity invokeActivity, ScannerTaskCallback callback) {
        final WifiManager wifiManager = (WifiManager) invokeActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final int subNetInt = wifiManager.getDhcpInfo().gateway;
        scannerTaskCallback = callback;
        subnetString = String.format(
                "%d.%d.%d",
                (subNetInt & 0xff),
                (subNetInt >> 8 & 0xff),
                (subNetInt >> 16 & 0xff));
        try {
            final JSONObject load = new JSONObject();
            load.put("type", "is_lighthub");
            load.put("data", new JSONObject());
            jsonRequest = new JSONObject();
            jsonRequest.put("load", load);
        } catch (JSONException ignore) { }
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
        for(int i = 0; i < 255; i++) {
            final String currentSubnet = subnetString + "." +i;
            try {
                final InetAddress currentAddress = InetAddress.getByName(currentSubnet);
                if (currentAddress.isReachable(TIMEOUT)) {
                    addIfLighthub(currentAddress);
                }
            } catch (IOException ioException) {
                scannerTaskCallback.onScanFailure();
            }
            finally {
                publishProgress(i);
            }
        }

        publishProgress(999);

        System.out.println("Found: " + onlineDeviceList.size() + " devices");
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
     * Sends a get request to the server to check if it is a lighthub.
     * If a true is returned adds it to the deviceList
     * @param address The to check address
     */
    private void addIfLighthub(final InetAddress address) {
        try {
            RequestBody body = RequestBody.create(JSON, "{\"load\": {\"type\": \"is_lighthub_server\", \"data\": {}}}");
            Request request = new Request.Builder()
                    .url("http://" + address.getHostAddress())
                    .post(body)
                    .build();
            String test = "http://" + address.getHostAddress();
            if(test.equals("http://192.168.1.101")) {
                System.out.println(test);
            }
            try {
                final Call call = client.newCall(request);
                final Response response = call.execute();
                final JSONObject myResponse = new JSONObject(response.body().string());
                if((boolean)myResponse.get("is_lighthub")) {
                    onlineDeviceList.add(address);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (JSONException jsonException) {
            System.out.println(jsonException.getMessage());
        }
    }
}