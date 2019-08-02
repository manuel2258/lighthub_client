/*
 * Date: 7/28/19 2:53 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.networking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

public class NetworkManager {

    @SuppressLint("DefaultLocale")
    public static String getCurrentSubnet(Activity invokeActivity) {
        final WifiManager wifiManager = (WifiManager) invokeActivity
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        final int subNetInt = wifiManager.getDhcpInfo().gateway;
        return String.format(
                "%d.%d.%d",
                (subNetInt & 0xff),
                (subNetInt >> 8 & 0xff),
                (subNetInt >> 16 & 0xff));
    }

    public static List<String> getSavedWifiConfigurations(Activity invokeActivity) {
        final WifiManager wifiManager = (WifiManager) invokeActivity
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        final List<String> configurationList = new ArrayList<>();

        for (WifiConfiguration configuration: wifiManager.getConfiguredNetworks()) {
            configurationList.add(configuration.SSID);
        }
        return configurationList;
    }

}
