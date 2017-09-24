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
 */

public class SportsTrackerSensorHubListener implements SensorHubListener {

    private final Context context;
    private final Resources resources;
    private final FunctionsActivity activity;

    public SportsTrackerSensorHubListener(Context context, Resources resources, FunctionsActivity activity){
        if(context == null)
            throw new IllegalArgumentException("null value is prohibited");
        this.context = context;
        this.resources = resources;
        this.activity = activity;
    }

    @Override
    public void onConnected(SensorHub sensorHub) {
        Toast.makeText(context, resources.getString(R.string.connected_to_device) + " " + sensorHub.getName(), Toast.LENGTH_SHORT).show();

        sensorHub.setSelectedSensor(sensorHub.getSensorList().get(0).getId());
        sensorHub.setMode("mode", "bg");
        sensorHub.setMode("prs_mr", "16");
        sensorHub.setMode("prs_osr", "32");

        sensorHub.start();
    }

    @Override
    public void onDisconnected(SensorHub sensorHub) {
        Toast.makeText(context, sensorHub.getName() + " " + resources.getString(R.string.disconnected_from_device), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionError(SensorHub sensorHub) {
        Toast.makeText(context, resources.getString(R.string.connection_error) + " " + sensorHub.getName(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSensorDataReceived(SensorHub sensorHub, SensorEvent sensorEvent) {
        if(sensorEvent.getDataId().equals("p") && activity != null){
            activity.pressureDataUpdated(sensorEvent.getSensorValue());
        }
    }

    @Override
    public void onModeChanged(SensorHub sensorHub, Mode mode) {

    }
}