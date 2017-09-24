package com.github.abysmalsb.sportstracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.github.abysmalsb.sportstrackerwithsensorhubnano.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PushUpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PushUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PushUpFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView counter;
    private CheckBox hasGoal;
    private EditText goalNumber;

    private PushUpCounter mPushUpCounter;
    private MeasurementsSmoother filter = new MeasurementsSmoother(8);

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

        counter = (TextView) view.findViewById(R.id.pushUpCounter);
        hasGoal = (CheckBox) view.findViewById(R.id.hasGoal);
        goalNumber = (EditText) view.findViewById(R.id.goalNumber);

        hasGoal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    enableGoalNumber(isChecked);
                }
            }
        );

        goalNumber.setEnabled(hasGoal.isChecked());

        return view;
    }

    private void enableGoalNumber(boolean enable){
        goalNumber.setEnabled(enable);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void pressureDataUpdated(double pressure) {
        if(mPushUpCounter == null){
            mPushUpCounter = new PushUpCounter(pressure);
        }
        counter.setText(mPushUpCounter.getCycleCount(pressure) + "");
    }
}
