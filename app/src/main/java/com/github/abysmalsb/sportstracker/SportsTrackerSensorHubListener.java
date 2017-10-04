package com.github.abysmalsb.sportstracker;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import com.github.abysmalsb.sportstrackerwithsensorhubnano.R;
import com.infineon.sen.comm.Model.Mode;
import com.infineon.sen.comm.SensorEvent;
import com.infineon.sen.comm.SensorHub;
import com.infineon.sen.comm.SensorHubListener;

/**
 * Created by Balazs Simon on 2017. 09. 11..
 * Implementation for SensorHubListener
 */

public class SportsTrackerSensorHubListener implements SensorHubListener {

    private final Context context;
    private final Resources resources;
    private final FunctionsActivity activity;
    private final String deviceName;

    public SportsTrackerSensorHubListener(Context context, Resources resources, FunctionsActivity activity, String deviceName){
        if(context == null)
            throw new IllegalArgumentException("null value is prohibited");
        this.context = context;
        this.resources = resources;
        this.activity = activity;
        this.deviceName = deviceName;
    }

    @Override
    public void onConnected(SensorHub sensorHub) {
        Toast.makeText(context, resources.getString(R.string.connected_to_device) + " " + deviceName, Toast.LENGTH_SHORT).show();

        sensorHub.setSelectedSensor(sensorHub.getSensorList().get(0).getId());
        sensorHub.setMode("mode", "bg");
        sensorHub.setMode("prs_mr", "16");
        sensorHub.setMode("prs_osr", "32");
        sensorHub.setMode("temp_mr", "4");
        sensorHub.setMode("temp_osr", "16");

        sensorHub.start();
    }

    @Override
    public void onDisconnected(SensorHub sensorHub) {
        Toast.makeText(context, deviceName + " " + resources.getString(R.string.disconnected_from_device), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionError(SensorHub sensorHub) {
        Toast.makeText(context, resources.getString(R.string.connection_error) + " " + deviceName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorDataReceived(SensorHub sensorHub, SensorEvent sensorEvent) {
        if(sensorEvent.getDataId().equals("a") && activity != null){
            activity.altitudeDataUpdated(sensorEvent.getSensorValue());
        }
    }

    @Override
    public void onModeChanged(SensorHub sensorHub, Mode mode) {

    }
}
