/*
 * Date: 8/5/19 2:32 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.networking;

import android.os.AsyncTask;

import com.protonmail.manuel2258.activitys.change.dates.DateRange;
import com.protonmail.manuel2258.activitys.change.dates.DateTime;
import com.protonmail.manuel2258.activitys.change.dates.RangeContainer;
import com.protonmail.manuel2258.activitys.change.dates.RangeContainerBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A Async OkHttpRequest
 */
public class AsyncRequestTask extends AsyncTask<Void, Integer, List<RangeContainer>> {

    private final AsyncRequestCallback callback;
    private final Call call;

    public AsyncRequestTask(Call call, AsyncRequestCallback callback) {
        this.callback = callback;
        this.call = call;
        call.timeout().timeout(30, TimeUnit.SECONDS);
    }

    @Override
    public List<RangeContainer> doInBackground(Void... voids) {
        List<RangeContainer> containers = new ArrayList<>();
        try {
            Response response = call.execute();
            final String responseMessage = response.body().string();
            final JSONArray times = new JSONObject(responseMessage).getJSONObject("load").getJSONArray("data");
            final RangeContainerBuilder rangeContainerBuilder = new RangeContainerBuilder();
            for (int i = 0; i < times.length(); i++) {
                JSONObject time = times.getJSONObject(i);
                rangeContainerBuilder.add(new DateRange(
                                new DateTime(time.getInt("s_h"),
                                        time.getInt("s_m")),
                                new DateTime(time.getInt("e_h"),
                                        time.getInt("e_m"))),
                        time.getInt("s_d"));
            }
            containers = rangeContainerBuilder.build();
        } catch (Exception exception) {
            callback.onException();
        }
        callback.onNewData(containers);
        return containers;
    }

}