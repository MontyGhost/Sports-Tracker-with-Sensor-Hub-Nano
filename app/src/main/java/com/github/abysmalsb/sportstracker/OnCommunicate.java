package com.github.abysmalsb.sportstracker;

import android.content.SharedPreferences;

/**
 * Created by Balazs Simon on 2017. 09. 24..
 */

public interface OnCommunicate {

    /**
     * This function have to result an audio signal meaning success
     */
    void playSuccessAudio();

    /**
     * This function have to result an audio signal meaning alert
     */
    void playAlertAudio();

    /**
     * Getter for sharedPreferences
     */
    SharedPreferences getSharedPreferences();

}
