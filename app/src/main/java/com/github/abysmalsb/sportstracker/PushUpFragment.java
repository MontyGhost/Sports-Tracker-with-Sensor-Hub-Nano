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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final String GOAL_ENABLED = "goalEnabled";
    private final String UPDATE_GOAL = "updateGoal";

    private OnGoalAchieved mActivity;

    private TextView counter;
    private CheckBox hasGoal;
    private EditText goalNumber;
    private Button startStopButton;
    private Button resetButton;
    private SharedPreferences prefs;

    private PushUpCounter mPushUpCounter;
    private MeasurementsSmoother mFilter = new MeasurementsSmoother(8);
    private boolean mCountingStarted = false;
    private boolean mAudioPlayed = false;
    private int mPushUps = 0;
    private int mPushUpGoal;

    public PushUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PushUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PushUpFragment newInstance(String param1, String param2) {
        PushUpFragment fragment = new PushUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_push_up, container, false);

        prefs = mActivity.getSharedPreferences();
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
        if (context instanceof OnGoalAchieved) {
            mActivity = (OnGoalAchieved) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    public interface OnGoalAchieved {
        void playSuccessAudio();
        SharedPreferences getSharedPreferences();
    }

    public void pressureDataUpdated(double pressure) {
        if(mCountingStarted){
            if(mPushUpCounter == null){
                mPushUpCounter = new PushUpCounter(pressure);
            }
            mPushUps = mPushUpCounter.getCycleCount(pressure);
            counter.setText(mPushUps + "");

            if(mPushUps >= mPushUpGoal && hasGoal.isChecked()){
                counter.setTextColor(Color.GREEN);
                if(!mAudioPlayed){
                    mActivity.playSuccessAudio();
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
