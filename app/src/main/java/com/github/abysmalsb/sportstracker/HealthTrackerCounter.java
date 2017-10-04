package com.github.abysmalsb.sportstracker;

/**
 * Created by Balazs Simon on 2017. 09. 24..
 */

public class HealthTrackerCounter {

    private final double mWindowsSize;    //it is equal with about 20cm vertical movement. It is perfect for push ups, sit ups
    private double mWindowUpper;
    private double mWindowLower;
    private int mPushUps;
    private boolean mUpReached;
    private boolean mBottomReached;
    private MeasurementsSmoother filter;

    public HealthTrackerCounter(double initialPressure, double windowsSize){
        mWindowsSize = windowsSize;
        mWindowLower = initialPressure - mWindowsSize / 2;
        mWindowUpper = initialPressure + mWindowsSize / 2;
        mPushUps = 0;
        mUpReached = false;
        mBottomReached = false;
        filter = new MeasurementsSmoother(8);
    }

    /**
     * It counts the up-down cycles that the user makes during workout. It uses a moving windows to prevent multiple counting
     * @param currentPressure
     * @return
     */
    public int getCycleCount(double currentPressure){
        double value = filter.averageIt(currentPressure);

        if (value > mWindowUpper){
            mWindowUpper = value;
            mWindowLower = value - mWindowsSize;
            if(mUpReached)
                mBottomReached = true;
        } else if (value < mWindowLower){
            mWindowLower = value;
            mWindowUpper = value + mWindowsSize;
            mUpReached = true;
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
