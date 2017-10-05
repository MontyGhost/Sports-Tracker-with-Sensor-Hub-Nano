package com.github.abysmalsb.sportstracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.abysmalsb.sportstrackerwithsensorhubnano.R;

public class SittingFragment extends Fragment implements SensorUpdate {

    private final int DEFAULT_MINUTES = 20;
    private final int REPEAT_IN_MINUTES = 300000; //five minutes in millis
    private final double ALTITUDE_DIFFERENCE = 0.3; //0.3 meter height difference when the user stands up. It is used as an offset for the threshold
    private final String MINUTES = "minutes";

    private OnCommunicate mCommunicate;

    private EditText sittingMinutes;
    private Button startStop;
    private TextView status;
    private TextView timerIndicator;
    private SharedPreferences prefs;
    private CountDownTimer variableTimer;
    private CountDownTimer fixTimer;

    private int mMinutesForSitting;
    private double mStartingPoint;
    private boolean isStartingPointInitialized;
    private boolean isStarted;
    private boolean isCountingDown;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sitting, container, false);

        prefs = mCommunicate.getSharedPreferences();

        status = (TextView) view.findViewById(R.id.statusText);

        timerIndicator = (TextView) view.findViewById(R.id.time_remaining);
        timerIndicator.setText(mMinutesForSitting + ":00");

        fixTimer = new CountDownTimer(REPEAT_IN_MINUTES, 1000) {

            public void onTick(long millisUntilFinished) {
                long minutes = milliToSec(millisUntilFinished) / 60;
                long seconds = milliToSec(millisUntilFinished) % 60;
                timerIndicator.setText(minutes + (seconds / 10 > 0 ? ":" : ":0") + seconds);
            }

            public void onFinish() {
                mCommunicate.playAlertAudio();
                fixTimer.start();
                isCountingDown = true;
            }
        };

        isStarted = false;
        startStop = (Button) view.findViewById(R.id.startSitting);
        startStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isStarted = !isStarted;
                if (isStarted) {
                    startStop.setText(getString(R.string.stop));
                    isStartingPointInitialized = false;
                    variableTimer = new CountDownTimer(mMinutesForSitting * 60 * 1000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            long minutes = milliToSec(millisUntilFinished) / 60;
                            long seconds = milliToSec(millisUntilFinished) % 60;
                            timerIndicator.setText(minutes + (seconds / 10 > 0 ? ":" : ":0") + seconds);
                        }

                        public void onFinish() {
                            mCommunicate.playAlertAudio();
                            fixTimer.start();
                            isCountingDown = true;
                        }
                    }.start();
                    isCountingDown = true;
                } else {
                    timerIndicator.setText(mMinutesForSitting + ":00");
                    startStop.setText(getString(R.string.start));
                    if (variableTimer != null) {
                        variableTimer.cancel();
                        fixTimer.cancel();
                        isCountingDown = false;
                    }
                }
            }
        });

        sittingMinutes = (EditText) view.findViewById(R.id.sitting_minutes_input);
        sittingMinutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMinutesForSitting = sittingMinutes.getText().toString().isEmpty() ? 0 : Integer.parseInt(sittingMinutes.getText().toString());
                timerIndicator.setText(mMinutesForSitting + ":00");

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(MINUTES, mMinutesForSitting);
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mMinutesForSitting = prefs.getInt(MINUTES, DEFAULT_MINUTES);
        sittingMinutes.setText(mMinutesForSitting + "");

        isStartingPointInitialized = false;
        isCountingDown = false;

        return view;
    }

    private long milliToSec(long millisec) {
        return millisec / 1000;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCommunicate) {
            mCommunicate = (OnCommunicate) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCommunicate = null;
    }

    /**
     * This function uses a fix threshold, updated on start
     * If you reach the "sitting for minutes" time, then you'll hear an alarm. It won't force you to stand up, but a new countdown, from 5 minutes will be started until you stand up, or click on the stop button.
     * If you stand up and then sit down, it will start counting down from the given time, not from 5 minutes.
     * Standing for only a moment is enough to reset the timer
     * @param altitude
     */
    @Override
    public void altitudeDataUpdated(double altitude) {

        //This function can run before initializeing the timers so we have to take care of that
        if (isStarted && variableTimer != null && fixTimer != null ) {
            //We have to initialize a starting point to use as a threshold
            if (!isStartingPointInitialized) {
                isStartingPointInitialized = true;
                mStartingPoint = altitude;
                return;
            }
            //
            if (mStartingPoint + ALTITUDE_DIFFERENCE <= altitude && isCountingDown) {
                status.setText(getString(R.string.standing));
                timerIndicator.setText(mMinutesForSitting + ":00");
                variableTimer.cancel();
                fixTimer.cancel();
                isCountingDown = false;
            } else if(mStartingPoint + ALTITUDE_DIFFERENCE > altitude && !isCountingDown) {
                status.setText(getString(R.string.sitting));
                variableTimer.start();
                isCountingDown = true;
            }
        }
    }
}
