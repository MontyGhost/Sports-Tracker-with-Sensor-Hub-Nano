package com.github.abysmalsb.sportstracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.github.abysmalsb.sportstrackerwithsensorhubnano.R;

public class PushUpFragment extends Fragment {

    private final String GOAL_ENABLED = "goalEnabled";
    private final String UPDATE_GOAL = "updateGoal";

    private OnCommunicate mCommunicate;

    private TextView counter;
    private CheckBox hasGoal;
    private EditText goalNumber;
    private Button startStopButton;
    private Button resetButton;
    private SharedPreferences prefs;

    private HealthTrackerCounter mPushUpCounter;
    //private MeasurementsSmoother mFilter = new MeasurementsSmoother(8);
    private boolean mCountingStarted = false;
    private boolean mAudioPlayed = false;
    private int mPushUps = 0;
    private int mPushUpGoal;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_push_up, container, false);

        prefs = mCommunicate.getSharedPreferences();
        counter = (TextView) view.findViewById(R.id.pushUpCounter);
        hasGoal = (CheckBox) view.findViewById(R.id.hasGoal);
        goalNumber = (EditText) view.findViewById(R.id.goalNumber);
        mPushUpGoal = Integer.parseInt(goalNumber.getText().toString());
        goalNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPushUpGoal = goalNumber.getText().toString().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(goalNumber.getText().toString());

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(UPDATE_GOAL, mPushUpGoal);
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        hasGoal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    goalNumber.setEnabled(isChecked);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(GOAL_ENABLED, isChecked);
                    editor.commit();
                }
            }
        );
        hasGoal.setChecked(prefs.getBoolean(GOAL_ENABLED, false));
        mPushUpGoal = prefs.getInt(UPDATE_GOAL, 20);
        goalNumber.setText(mPushUpGoal + "");
        goalNumber.setEnabled(hasGoal.isChecked());

        startStopButton = (Button) view.findViewById(R.id.startStop);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mCountingStarted = !mCountingStarted;
                startStopButton.setText(mCountingStarted ? getString(R.string.pause) : getString(R.string.continue_text));
            }
        });
        resetButton = (Button) view.findViewById(R.id.reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mPushUpCounter != null){
                    mPushUpCounter.resetCounter();
                }
                startStopButton.setText(getString(R.string.start));
                mCountingStarted = false;
                mPushUps = 0;
                counter.setText(mPushUps + "");
                counter.setTextColor(Color.WHITE);
            }
        });

        return view;
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

    public void altitudeDataUpdated(double altitude) {
        if(mCountingStarted){
            if(mPushUpCounter == null){
                mPushUpCounter = new HealthTrackerCounter(altitude, 0.2);
            }
            mPushUps = mPushUpCounter.getCycleCount(altitude);
            counter.setText(mPushUps + "");

            if(mPushUps >= mPushUpGoal && hasGoal.isChecked()){
                counter.setTextColor(Color.GREEN);
                if(!mAudioPlayed){
                    mCommunicate.playSuccessAudio();
                    mAudioPlayed = true;
                }
            }
            else{
                counter.setTextColor(Color.WHITE);
                mAudioPlayed = false;
            }
        }
    }
}
