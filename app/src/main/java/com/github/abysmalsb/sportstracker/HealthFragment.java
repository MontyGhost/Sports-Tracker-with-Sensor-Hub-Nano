package com.github.abysmalsb.sportstracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
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
import android.widget.Toast;

import com.github.abysmalsb.sportstrackerwithsensorhubnano.R;


public class HealthFragment extends Fragment implements SensorUpdate {

    private final String HEIGHT = "height";
    private final String PHONE_NUMBER = "phoneNumber";
    private final String MESSAGE = "message";
    private final String UPPER_LIMIT = "upperLimit";
    private final String LOWER_LIMIT = "lowerLimit";
    private final String FALL_DETECTION = "fallDetection";
    private final String ALTITUDE_LOCK = "altitudeLock";

    private final int WAITING_BEFORE_CALL_FOR_HELP_IN_MILLIS = 15000;

    private OnCommunicate mCommunicate;
    private SharedPreferences prefs;

    private TextView textHeight;
    private TextView textPhoneNumber;
    private TextView textMessage;
    private TextView textUpperLimit;
    private TextView textLowerLimit;
    private EditText personHeight;
    private EditText phoneNumber;
    private EditText message;
    private EditText upperLimit;
    private EditText lowerLimit;
    private CheckBox fallDetection;
    private CheckBox altitudeLock;
    private Button startStop;

    private int mHeight;
    private String mPhoneNumber;
    private String mMessage;
    private int mUpperLimit;
    private int mLowerLimit;
    private boolean mStarted;
    private long firstFallTime;
    private boolean firstFallCycle;
    private boolean messageSent;

    private double mStartingPoint;
    private boolean isStartingPointInitialized;
    private MeasurementsSmoother smoother;
    private FallDetector mFallDetector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_health, container, false);
        prefs = mCommunicate.getSharedPreferences();

        textHeight = (TextView) view.findViewById(R.id.person_height);
        textPhoneNumber = (TextView) view.findViewById(R.id.phone_number);
        textMessage = (TextView) view.findViewById(R.id.message);
        textUpperLimit = (TextView) view.findViewById(R.id.upper_limit);
        textLowerLimit = (TextView) view.findViewById(R.id.lower_limit);

        startStop = (Button) view.findViewById(R.id.startMonitoring);
        startStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateStarted(!mStarted);
            }
        });

        personHeight = (EditText) view.findViewById(R.id.height_input);
        personHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHeight = personHeight.getText().toString().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(personHeight.getText().toString());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(HEIGHT, mHeight);
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mHeight = prefs.getInt(HEIGHT, 0);
        personHeight.setText(mHeight + "");

        phoneNumber = (EditText) view.findViewById(R.id.phone_number_input);
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPhoneNumber = phoneNumber.getText().toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(PHONE_NUMBER, mPhoneNumber);
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mPhoneNumber = prefs.getString(PHONE_NUMBER, "");
        phoneNumber.setText(mPhoneNumber);

        message = (EditText) view.findViewById(R.id.emergency_message_input);
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMessage = message.getText().toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(MESSAGE, mMessage);
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mMessage = prefs.getString(MESSAGE, getString(R.string.i_fell_message));
        message.setText(mMessage);

        upperLimit = (EditText) view.findViewById(R.id.upper_limit_input);
        upperLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUpperLimit = upperLimit.getText().toString().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(upperLimit.getText().toString());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(UPPER_LIMIT, mUpperLimit);
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mUpperLimit = prefs.getInt(UPPER_LIMIT, 0);
        upperLimit.setText(mUpperLimit + "");

        lowerLimit = (EditText) view.findViewById(R.id.lower_limit_input);
        lowerLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLowerLimit = lowerLimit.getText().toString().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(lowerLimit.getText().toString());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(LOWER_LIMIT, mLowerLimit);
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mLowerLimit = prefs.getInt(LOWER_LIMIT, 0);
        lowerLimit.setText(mLowerLimit + "");

        fallDetection = (CheckBox) view.findViewById(R.id.fall_detection);
        fallDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                     @Override
                                                     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                         enableFallDetectionAttributes(isChecked);

                                                         SharedPreferences.Editor editor = prefs.edit();
                                                         editor.putBoolean(FALL_DETECTION, isChecked);
                                                         editor.commit();

                                                         updateStarted(false);
                                                     }
                                                 }
        );
        fallDetection.setChecked(prefs.getBoolean(FALL_DETECTION, false));
        enableFallDetectionAttributes(fallDetection.isChecked());

        altitudeLock = (CheckBox) view.findViewById(R.id.altitude_lock);
        altitudeLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        enableAltitudeLockAttributes(isChecked);

                                                        SharedPreferences.Editor editor = prefs.edit();
                                                        editor.putBoolean(ALTITUDE_LOCK, isChecked);
                                                        editor.commit();

                                                        updateStarted(false);
                                                    }
                                                }
        );
        altitudeLock.setChecked(prefs.getBoolean(ALTITUDE_LOCK, false));
        enableAltitudeLockAttributes(altitudeLock.isChecked());

        smoother = new MeasurementsSmoother(10);

        firstFallCycle = true;
        messageSent = false;
        updateStarted(false);

        return view;
    }

    private void updateStarted(boolean started) {
        mStarted = started;
        startStop.setText(mStarted ? getString(R.string.stop) : getString(R.string.start));
        if (mStarted) {
            isStartingPointInitialized = false;
            messageSent = false;
        }
    }

    private void enableFallDetectionAttributes(boolean enable) {
        personHeight.setEnabled(enable);
        phoneNumber.setEnabled(enable);
        message.setEnabled(enable);
        textHeight.setEnabled(enable);
        textPhoneNumber.setEnabled(enable);
        textMessage.setEnabled(enable);
    }

    private void enableAltitudeLockAttributes(boolean enable) {
        upperLimit.setEnabled(enable);
        lowerLimit.setEnabled(enable);
        textUpperLimit.setEnabled(enable);
        textLowerLimit.setEnabled(enable);
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

    @Override
    public void altitudeDataUpdated(double altitude) {
        if(smoother != null){
            double value = smoother.averageIt(altitude);

            if (mStarted) {
                if (fallDetection.isChecked()) {
                    if(mFallDetector == null){
                        mFallDetector = new FallDetector(63, 1500, -0.03, value, 0.3);
                    }
                    if(mFallDetector.isFallen(value)){
                        mCommunicate.playAlertAudio();

                        if(firstFallCycle){
                            firstFallCycle = false;
                            firstFallTime = System.currentTimeMillis();
                        }
                        else{
                            if(firstFallTime + WAITING_BEFORE_CALL_FOR_HELP_IN_MILLIS < System.currentTimeMillis()){
                                if(!messageSent){
                                    sendSMS();
                                    messageSent = true;
                                }
                            }
                        }
                    }
                    else{
                        firstFallCycle = true;
                    }
                }
                if (altitudeLock.isChecked()) {
                    if (!isStartingPointInitialized) {
                        isStartingPointInitialized = true;
                        mStartingPoint = value;
                    }
                    if (value - mStartingPoint > mUpperLimit / 100.0 || mStartingPoint - value > mLowerLimit / 100.0) {
                        mCommunicate.playAlertAudio();
                    }
                }
            }
        }
    }

    private void sendSMS() {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber.getText().toString(), null, message.getText().toString(), null, null);
            Toast.makeText(this.getContext(), getString(R.string.emergency_message_sent) + "\n" + phoneNumber.getText().toString() + " --> \"" + message.getText().toString() + "\"", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(this.getContext(), getString(R.string.error_sms), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
