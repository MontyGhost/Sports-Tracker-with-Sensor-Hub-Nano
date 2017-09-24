package com.github.abysmalsb.sportstracker;

import android.util.Log;

/**
 * Created by Balazs Simon on 2017. 09. 24..
 */

public class PushUpCounter {

    private final double WINDOW_SIZE = 0.02;    //it is equal with about 20cm vertical movement. It is perfect for push ups, sit ups
    private double mWindowUpper;
    private double mWindowLower;
    private int mPushUps;
    private boolean mUpReached;
    private boolean mBottomReached;
    private MeasurementsSmoother filter;

    public PushUpCounter(double initialPressure){
        mWindowLower = initialPressure - WINDOW_SIZE / 2;
        mWindowUpper = initialPressure + WINDOW_SIZE / 2;
        mPushUps = 0;
        mUpReached = false;
        mBottomReached = false;
        filter = new MeasurementsSmoother(8);
    }

    public int getCycleCount(double currentPressure){
        Log.i("frag", filter.averageIt(currentPressure) + " " + currentPressure + " " + mPushUps + " " + mUpReached + " " + mBottomReached);

        double value = filter.averageIt(currentPressure);

        if (value > mWindowUpper){
            mWindowUpper = value;
            mWindowLower = value - WINDOW_SIZE;
            mUpReached = true;
        } else if (value < mWindowLower){
            mWindowLower = value;
            mWindowUpper = value + WINDOW_SIZE;
            if(mUpReached)
                mBottomReached = true;
        }

        if (mUpReached && mBottomReached){
            mUpReached = false;
            mBottomReached = false;
            mPushUps++;
        }

        return mPushUps;
    }

    public void resetCounter(){
        mPushUps = 0;
    }
}
