package com.github.abysmalsb.sportstracker;

/**
 * Created by Balazs Simon on 2017. 09. 24..
 */

public class MeasurementsSmoother {
    private final int mSize;
    private double mMeasurements[];
    private int mNextIndex;

    public MeasurementsSmoother(int numberOfElements){
        mSize = numberOfElements;
        mMeasurements = new double[mSize];
        mNextIndex = 0;
    }

    public double averageIt(double measurement){
        double sum = 0.0;

        mMeasurements[mNextIndex] = measurement;
        mNextIndex++;

        if(mSize == mNextIndex)
            mNextIndex = 0;

        for(double value : mMeasurements){
            sum += value;
        }

        return sum / mSize;
    }
}
