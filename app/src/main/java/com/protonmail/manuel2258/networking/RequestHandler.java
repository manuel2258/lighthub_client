/*
 * Date: 7/28/19 2:41 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.networking;

import android.annotation.SuppressLint;
import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/** A abstract Singleton to handle http Requests */
public abstract class RequestHandler {
    
    private static RequestHandler instance;
    public static RequestHandler getInstance() {
        if(instance == null) {
            instance = new OkHttpRequestHandler();
        }
        return instance;
    }

    /**
     * Checks whether the address is a lighthub
     * @param address The to check address
     * @return True if lighthub, false otherwise
     * @throws IOException If there is a problem with the connection to the address
     */
    public abstract boolean isLightHub(String address) throws IOException;

    /**
     * Sets up the device to use the a given wifi network
     * @param address The to set up address
     * @param name The network ssid
     * @param password The network password
     * @return True if setup correctly, false otherwise
     * @throws IOException If there is a problem with the connection to the address
     */
    public abstract boolean setupDevice(String address, String name, String password) throws IOException;

    /**
     * Gets the current color from the device
     * @param address The to get from address
     * @return The color int (-1 if jsonException)
     * @throws IOException If there is a problem with the connection to the address
     */
    public abstract int getColor(String address) throws IOException;

    /**
     * Sets the current color from the device
     * @param address The to set address
     * @param color The to set color
     * @return True if color was set, false otherwise
     * @throws IOException If there is a problem with the connection to the address
     */
    public abstract boolean setColor(String address, int color) throws IOException;

    /**
     * Gets the current used mode from the device
     * @param address The to get from address
     * @return The currently used mode
     * @throws IOException If there is a problem with the connection to the address
     */
    public abstract int getMode(String address) throws IOException;

    /**
     * Sets the current used mode from the device
     * @param address The to get from address
     * @param mode The to set to mode
     * @return True if set successfully, false otherwise
     * @throws IOException If there is a problem with the connection to the address
     */
    public abstract boolean setMode(String address, int mode) throws IOException;

    /**
     * Gets the current used timeRanges from the device
     * @param address The to get from address
     * @return A List of DateRanges representing the timeRanges
     */
    public abstract void getTimes(String address, AsyncRequestCallback callback);

    /**
     * Adds a DateRange to the device
     * @param address The to add to address
     * @param dateRange The to add dateRange
     * @return The updated DateRanges on the device
     */
    public abstract void addTime(String address, String dateRange, AsyncRequestCallback callback);

    /**
     * Removes a DateRange from the device
     * @param address The to remove from address
     * @param dateRange The to remove DateRange
     * @return The updated DateRanges on the device
     */
    public abstract void removeTime(String address, String dateRange, AsyncRequestCallback callback);
}

/** A RequestHandler implementation using the OkHttp Library */
class OkHttpRequestHandler extends RequestHandler {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client;

    public OkHttpRequestHandler() {
        client = new OkHttpClient();
    }

    @Override
    public boolean isLightHub(String address) throws IOException {
        boolean isLightHub = false;
        try {
            final String message = "{\"load\": {\"type\": \"is_lighthub\", " +
                    "\"request_type\": \"get\", \"data\": {}}}";
            final JSONObject response = makeCall(address, message);
            if ((boolean) response.get("is_lighthub")) {
                isLightHub = true;
            }

        } catch (JSONException jsonException) {
            System.out.println(jsonException.getMessage());
        }
        return isLightHub;
    }

    @Override
    public boolean setupDevice(String address, String name, String password) throws IOException {
        boolean successful = false;
        try {
            final String message = String.format("{\"load\": {\"type\": \"set_network\", " +
                    "\"request_type\": \"post\"," +
                    "\"data\": { \"name\": %s, \"password\": \"%s\"}}}", name, password);
            final JSONObject response = makeCall(address, message);
            if ((boolean) response.get("received")) {
                successful = true;
            }

        } catch (JSONException jsonException) {
            System.out.println(jsonException.getMessage());
        }
        return successful;
    }

    @Override
    public int getColor(String address) throws IOException {
        int returnColor = -1;
        try {
            final String message = "{\"load\": {\"type\": \"get_color\", " +
                    "\"request_type\": \"get\", \"data\": {}}}";
            final JSONObject response = makeCall(address, message);
            final JSONObject colorJson = response.getJSONObject("load")
                    .getJSONObject("data");
            returnColor = Color.rgb(colorJson.getInt("r"),
                    colorJson.getInt("g"),
                    colorJson.getInt("b"));
        } catch (JSONException jsonException) {
            System.out.println(jsonException.getMessage());
        }
        return returnColor;
    }

    @Override
    public boolean setColor(String address, int color) throws IOException {
        boolean successful = false;

        try {
            int[] colorValues = colorToIntArray(color);
            @SuppressLint("DefaultLocale") final String message = String.format("{\"load\": {\"type\": \"set_color\", " +
                    "\"request_type\": \"post\"," +
                    "\"data\": { \"r\": %d, \"g\": %d, \"b\": %d}}}",
                    colorValues[0], colorValues[1], colorValues[2]);
            final JSONObject response = makeCall(address, message);
            if ((boolean) response.get("received")) {
                successful = true;
            }

        } catch (JSONException jsonException) {
            System.out.println(jsonException.getMessage());
        }
        return successful;
    }

    @Override
    public int getMode(String address) throws IOException {
        int returnMode = -1;
        try {
            final String message = "{\"load\": {\"type\": \"get_mode\", " +
                    "\"request_type\": \"get\", \"data\": {}}}";
            final JSONObject response = makeCall(address, message);
            returnMode = response.getJSONObject("load")
                    .getInt("data");
        } catch (JSONException jsonException) {
            System.out.println(jsonException.getMessage());
        }
        return returnMode;
    }

    @Override
    public boolean setMode(String address, int mode) throws IOException {
        boolean successful = false;

        try {
            @SuppressLint("DefaultLocale") final String message = String.format("{\"load\": {\"type\": \"set_mode\", " +
                            "\"request_type\": \"post\"," +
                            "\"data\": { \"new_mode\": %d}}}", mode);
            final JSONObject response = makeCall(address, message);
            if ((boolean) response.get("received")) {
                successful = true;
            }

        } catch (JSONException jsonException) {
            System.out.println(jsonException.getMessage());
        }
        return successful;
    }

    @Override
    public void getTimes(String address, AsyncRequestCallback callback) {
        final String message = "{\"load\": {\"type\": \"get_times\", " +
                "\"request_type\": \"get\", \"data\": {}}}";
        makeAsyncCall(address, message, callback);
    }

    @Override
    public void addTime(String address, String dateRange, AsyncRequestCallback callback) {
        @SuppressLint("DefaultLocale") final String message = String.format("{\"load\": {\"type\": \"add_time\", " +
                "\"request_type\": \"post\"," +
                "\"data\": %s}}", dateRange);
        makeAsyncCall(address, message, callback);
    }

    @Override
    public void removeTime(String address, String dateRange, AsyncRequestCallback callback) {
        @SuppressLint("DefaultLocale") final String message = String.format("{\"load\": {\"type\": \"remove_time\", " +
                "\"request_type\": \"post\"," +
                "\"data\": %s}}", dateRange);
        makeAsyncCall(address, message, callback);
    }


    /**
     * Makes a call to the given address using json String
     * @param address The to call to address
     * @param jsonMessage The to send body message
     * @return The response JsonObject
     * @throws IOException If there is a problem with the connection to the address
     * @throws JSONException If either the message or response can't be parsed
     */
    private JSONObject makeCall(String address, String jsonMessage) throws IOException, JSONException {
        RequestBody body = RequestBody.create(JSON, jsonMessage);
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();

        final Call call = client.newCall(request);
        final Response response = call.execute();
        final String responseMessage = response.body().string();
        return new JSONObject(responseMessage);
    }

    /**
     * Makes a call async to the given address using json String
     * @param address The to call to address
     * @param jsonMessage The to send body message
     * @param callback The to execute callback
     */
    private void makeAsyncCall(String address, String jsonMessage, AsyncRequestCallback callback) {
        RequestBody body = RequestBody.create(JSON, jsonMessage);
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();

        Call call = client.newCall(request);
        new AsyncRequestTask(call, callback).execute();
    }

    /**
     * Converts a int value into a unsigned int array
     * @param color The to convert color
     * @return A 3 long int array
     */
    private int[] colorToIntArray(int color) {
        byte[] bytes = ByteBuffer.allocate(4).putInt(color).array();
        int[] colorValues = new int[3];
        for (int i = 0; i < 3; i++) {
            colorValues[i] = (int)((bytes[i+1] & 0xFF));
        }
        return colorValues;
    }

}
