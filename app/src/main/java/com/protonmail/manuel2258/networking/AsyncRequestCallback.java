/*
 * Date: 8/5/19 1:09 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.networking;

import com.protonmail.manuel2258.activitys.change.dates.RangeContainer;

import java.util.List;

public interface AsyncRequestCallback {
    void onNewData(List<RangeContainer> containers);
    void onException();
}
