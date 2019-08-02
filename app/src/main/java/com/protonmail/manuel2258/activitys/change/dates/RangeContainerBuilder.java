/*
 * Date: 8/2/19 11:24 AM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

/*
 * Date: 8/2/19 11:02 AM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.activitys.change.dates;

import java.util.ArrayList;
import java.util.List;

/** A builder class to build a list of RangeContainers */
public class RangeContainerBuilder {

    private final List<RangeContainer> rangeContainers = new ArrayList<>();

    /**
     * Adds the DateRange to a container or creates a new one for it if non fits
     * @param dateRange The to add dateRange
     * @param day The to add to day
     */
    public void add(DateRange dateRange, int day) {
        boolean added = false;
        for (RangeContainer rangeContainer : rangeContainers) {
            if (rangeContainer.addIfContained(dateRange, day)) {
                added = true;
                break;
            }
        }
        if(rangeContainers.size() == 0 || !added) {
            rangeContainers.add(new RangeContainer(dateRange, day));
        }
    }

    /**
     * Builds a list of RangeContainers
     * @return The builded List
     */
    public List<RangeContainer> build() {
        return rangeContainers;
    }

}
