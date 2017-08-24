package com.github.abysmalsb.sportstrackerwithsensorhubnano;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Set;

public class BluetoothSelectorActivity extends AppCompatActivity {

    private ListView btDevices;
    private Button btScan;
    private BluetoothDevice[] devices;
    private CustomAdapter adapter;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_selector);
        setTitle(R.string.title_bluetooth_selector);

        btDevices = (ListView) findViewById(R.id.btDevicesList);
        btScan = (Button) findViewById(R.id.scanButton);

        adapter = new CustomAdapter();
        devices = getPairedBTDevices();

        btDevices.setOnItemClickListener(
              new AdapterView.OnItemClickListener(){
                  @Override
                  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                      connect(devices[position].getAddress());
                  }
              }
        );

        btScan.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devices = getPairedBTDevices();
                adapter.notifyDataSetChanged();

                if(devices.length == 0){
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(getApplicationContext(), R.string.toast_no_device, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        btDevices.setAdapter(adapter);
    }

    private void connect(String address) {
        Intent intent = new Intent(BluetoothSelectorActivity.this, FunctionsActivity.class);
        intent.putExtra("deviceAddress", address);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private BluetoothDevice[] getPairedBTDevices() {
        Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        return devices.toArray(new BluetoothDevice[devices.size()]);
    }

    private boolean isRecognized(BluetoothDevice d) {
        String deviceName = d.getName().toLowerCase();
        return deviceName.equals("ifx_nanohub") || deviceName.equals("ifx_senhub");
    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return devices.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.custom_layout, null);
            TextView deviceName = (TextView)convertView.findViewById(R.id.deviceName);
            TextView deviceAddress = (TextView)convertView.findViewById(R.id.macAddress);

            if(isRecognized(devices[position])){
                deviceName.setTextColor(Color.BLACK);
                deviceAddress.setTextColor(Color.BLACK);
            }
            else{
                deviceName.setTextColor(Color.RED);
                deviceAddress.setTextColor(Color.RED);
            }

            deviceName.setText(devices[position].getName());
            deviceAddress.setText(devices[position].getAddress());
            return convertView;
        }
    }
}
