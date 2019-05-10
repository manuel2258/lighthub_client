/*
 * Date: 5/2/19 6:35 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.networking;

import java.net.InetAddress;
import java.util.Collection;

/**
 * Callback interface to report on the scanning task.
 */
public interface ScannerTaskCallback {

    /**
     * Called when the scanning finished and returns a device collection.
     * @param devices Collection of online devices
     */
    void onScanSuccess(Collection<InetAddress> devices);

    /**
     * Called when a exception happens in the task.
     */
    void onScanFailure();

    /**
     * Called when a scan of a subnet finished.
     * @param currentState Integer between 0 and 255 indicating the current address
     */
    void onScanUpdate(int currentState);
}
