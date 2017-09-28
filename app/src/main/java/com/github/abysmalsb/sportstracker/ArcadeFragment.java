package com.github.abysmalsb.sportstracker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.abysmalsb.sportstrackerwithsensorhubnano.R;

import java.util.Random;


public class ArcadeFragment extends Fragment implements SensorUpdate {

    private OnCommunicate mCommunicate;

    private TextView targetValue;
    private TextView currentValue;
    private TextView recordValue;
    private Button generate;

    private MeasurementsSmoother smoother;
    private Random rand;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arcade, container, false);

        targetValue = (TextView)view.findViewById(R.id.target_value);
        currentValue = (TextView)view.findViewById(R.id.current_value);
        recordValue = (TextView)view.findViewById(R.id.record_value);
        generate = (Button)view.findViewById(R.id.generate_new);

        smoother = new MeasurementsSmoother(10);
        rand = new Random();

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

    @Override
    public void altitudeDataUpdated(double altitude) {
        if(smoother!=null){

        }
    }
}
