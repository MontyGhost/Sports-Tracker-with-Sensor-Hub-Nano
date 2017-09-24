package com.github.abysmalsb.sportstracker;

import android.content.SharedPreferences;

/**
 * Created by Balazs Simon on 2017. 09. 24..
 */

public interface OnCommunicate {

    void playSuccessAudio();

    void playAlertAudio();

    SharedPreferences getSharedPreferences();

}
