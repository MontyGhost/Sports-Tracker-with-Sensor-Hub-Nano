package com.github.abysmalsb.sportstracker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.abysmalsb.sportstrackerwithsensorhubnano.R;

public class ArcadeFragment extends Fragment implements SensorUpdate {

    private final double ERROR_TOLERANCE = 0.05;

    private OnCommunicate mCommunicate;

    private TextView targetValue;
    private TextView currentValue;
    private TextView recordValue;
    private Button generate;

    private double target;
    private double currentRecord;
    private double throwingPeak;
    private double initialValue;
    private boolean initialValueNeedsToBeUpdated;
    private boolean throwingUpdated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arcade, container, false);

        targetValue = (TextView) view.findViewById(R.id.target_value);
        currentValue = (TextView) view.findViewById(R.id.current_value);
        recordValue = (TextView) view.findViewById(R.id.record_value);
        generate = (Button) view.findViewById(R.id.generate_new);
        generate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                generateNewTarget();
            }
        });

        throwingPeak = Integer.MIN_VALUE;

        generateNewTarget();

        return view;
    }

    /**
     * It will generate a new target
     * The range of the generated target varies between 0.5 - 2 m from the starting height
     */
    private void generateNewTarget() {
        throwingUpdated = false;
        initialValueNeedsToBeUpdated = true;
        throwingPeak = Integer.MIN_VALUE;
        currentRecord = Integer.MIN_VALUE;
        target = Math.random() * 1.5 + 0.5; //random double with [0.5, 2] meter range
        targetValue.setText(String.format("%.2f", target) + " m");
        recordValue.setText("0.0 m");
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
     * Record will be updated with the closest thworing peak to the target
     * A valid throw must end under the 0 m relative height
     * @param altitude
     */
    @Override
    public void altitudeDataUpdated(double altitude) {

        if (initialValueNeedsToBeUpdated) {
            initialValueNeedsToBeUpdated = false;
            initialValue = altitude;
        }

        if (altitude > initialValue) {
            throwingUpdated = false;
            if (altitude > throwingPeak) {
                throwingPeak = altitude;
            }
        } else {
            if(!throwingUpdated && Math.abs(target - throwingPeak + initialValue) < Math.abs(target - currentRecord + initialValue)){
                mCommunicate.playSuccessAudio();
                currentRecord = throwingPeak;
                recordValue.setText(String.format("%.2f", currentRecord - initialValue) + " m");
            }
            throwingUpdated = true;
            throwingPeak = Integer.MIN_VALUE;
        }

        if (currentValue != null){
            currentValue.setText(String.format("%.2f", altitude - initialValue) + " m");
        }
    }
}
