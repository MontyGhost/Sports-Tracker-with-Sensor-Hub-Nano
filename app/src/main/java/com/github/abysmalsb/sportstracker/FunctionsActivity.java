package com.github.abysmalsb.sportstracker;

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

public class FunctionsActivity extends AppCompatActivity {

    private SensorHub sensorHub;
    private Fragment fragmentDisplayed;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_pushups:
                    fragmentDisplayed = new PushUpFragment();
                    transaction.replace(R.id.content, fragmentDisplayed).commit();
                    return true;
                case R.id.navigation_steps:
                    fragmentDisplayed = new StepsFragment();
                    transaction.replace(R.id.content, fragmentDisplayed).commit();
                    return true;
                case R.id.navigation_sitting:
                    fragmentDisplayed = new SittingFragment();
                    transaction.replace(R.id.content, fragmentDisplayed).commit();
                    return true;
                case R.id.navigation_help:
                    fragmentDisplayed = new HealthFragment();
                    transaction.replace(R.id.content, fragmentDisplayed).commit();
                    return true;
                case R.id.navigation_settings:
                    fragmentDisplayed = new SettingsFragment();
                    transaction.replace(R.id.content, fragmentDisplayed).commit();
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
        //TODO: ha nincsenek megadva az adatok (súly), akkor a Settings-szel induljon az app?
        fragmentDisplayed = new PushUpFragment();
        transaction.replace(R.id.content, fragmentDisplayed).commit();

        String deviceAddress = getIntent().getStringExtra("deviceAddress");

        sensorHub = new SensorHub(getApplicationContext(), deviceAddress);

        SportsTrackerSensorHubListener listener = new SportsTrackerSensorHubListener(getApplicationContext(), getResources(), this);

        sensorHub.addSensorHubListener(listener);
        sensorHub.connect();
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
        sensorHub.disconnect();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void pressureDataUpdated(double value){
        if(fragmentDisplayed == null)
            return;

        if(fragmentDisplayed instanceof PushUpFragment){
            ((PushUpFragment) fragmentDisplayed).pressureDataUpdated(value);
        }
    }
}
