package com.github.abysmalsb.sportstracker;

/**
 * Created by Balazs Simon on 2017. 09. 27..
 * Uniforming the Fragments to have this function
 */

public interface SensorUpdate {
    /**
     * The fragments will receive the updated altitude data using this function.
     * @param altitude
     */
    void altitudeDataUpdated(double altitude);
}
