package com.github.abysmalsb.sportstracker;

import android.util.Log;

/**
 * Created by Balazs Simon on 2017. 09. 28..
 */

public class FallDetector {
    private final int size;
    private final double averageDifferenceThreshold;
    private final double measurements[];
    private int currentIndex;
    private double totalDifference;
    private double heightAtFall;
    private double standUpThreshold;
    private double previousMeasurement;
    private boolean isFallenInAPreviousCycle;

    public FallDetector(int millisBetweenMeasurements, int windowsSizeInMillis, double averageDifferenceThreshold, double initialData, double standUpThreshold){
        size = (int)(windowsSizeInMillis / millisBetweenMeasurements);
        measurements = new double[size];
        previousMeasurement = initialData;
        totalDifference = 0;
        this.averageDifferenceThreshold = averageDifferenceThreshold;
        currentIndex = 0;
        totalDifference = 0.0;
        heightAtFall = 0.0;
        this.standUpThreshold = standUpThreshold;
        isFallenInAPreviousCycle = false;
    }

    public boolean isFallen(double currentAltitude){

        boolean isFallen = true;

        totalDifference -= measurements[currentIndex];
        measurements[currentIndex] = currentAltitude - previousMeasurement;
        previousMeasurement = currentAltitude;
        totalDifference += measurements[currentIndex];
        currentIndex++;
        if(currentIndex >= size)
            currentIndex = 0;

        if(!isFallenInAPreviousCycle){
            isFallen = totalDifference / size < averageDifferenceThreshold;
        }

        if(isFallen && !isFallenInAPreviousCycle){
            heightAtFall = currentAltitude;
            isFallenInAPreviousCycle = true;
        }

        if(isFallen && heightAtFall + standUpThreshold < currentAltitude){
            isFallen = false;
            isFallenInAPreviousCycle = false;
        }

        Log.i("teszt log: ", heightAtFall + standUpThreshold + " " + currentAltitude + " " + totalDifference + " " + size + " " + averageDifferenceThreshold + " " + isFallen);

        return isFallen;
    }
}
