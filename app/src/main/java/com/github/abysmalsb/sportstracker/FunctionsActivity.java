package com.github.abysmalsb.sportstracker;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.abysmalsb.sportstrackerwithsensorhubnano.R;
import com.infineon.sen.comm.SensorHub;

public class FunctionsActivity extends AppCompatActivity implements OnCommunicate {

    public static final String PREFS_NAME = "FunctionsPreferences";

    private SensorHub mSensorHub;
    private MediaPlayer mPlaySuccess;
    private MediaPlayer mPlayAlert;
    private Fragment mFragmentDisplayed;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_pushups:
                    mFragmentDisplayed = new PushUpFragment();
                    transaction.replace(R.id.content, mFragmentDisplayed).commit();
                    return true;
                case R.id.navigation_steps:
                    mFragmentDisplayed = new StepsFragment();
                    transaction.replace(R.id.content, mFragmentDisplayed).commit();
                    return true;
                case R.id.navigation_sitting:
                    mFragmentDisplayed = new SittingFragment();
                    transaction.replace(R.id.content, mFragmentDisplayed).commit();
                    return true;
                case R.id.navigation_help:
                    mFragmentDisplayed = new HealthFragment();
                    transaction.replace(R.id.content, mFragmentDisplayed).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        mFragmentDisplayed = new PushUpFragment();
        transaction.replace(R.id.content, mFragmentDisplayed).commit();

        String deviceAddress = getIntent().getStringExtra(BluetoothSelectorActivity.DEVICE_ADDRESS);
        String deviceName = getIntent().getStringExtra(BluetoothSelectorActivity.DEVICE_NAME);

        mSensorHub = new SensorHub(getApplicationContext(), deviceAddress);

        SportsTrackerSensorHubListener listener = new SportsTrackerSensorHubListener(getApplicationContext(), getResources(), this, deviceName);

        mSensorHub.addSensorHubListener(listener);
        mSensorHub.connect();

        mPlaySuccess = MediaPlayer.create(this, R.raw.success);
        mPlayAlert = MediaPlayer.create(this, R.raw.alert);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
            case android.R.id.home:
                leave();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        leave();
    }

    private void leave(){
        mSensorHub.disconnect();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void altitudeDataUpdated(double value){
        if(mFragmentDisplayed == null)
            return;

        if(mFragmentDisplayed instanceof SensorUpdate){
            ((SensorUpdate) mFragmentDisplayed).altitudeDataUpdated(value);
        }
    }

    @Override
    public void playSuccessAudio() {
        mPlaySuccess.start();
    }

    @Override
    public void playAlertAudio() {
        mPlayAlert.start();
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(PREFS_NAME, 0);
    }
}
