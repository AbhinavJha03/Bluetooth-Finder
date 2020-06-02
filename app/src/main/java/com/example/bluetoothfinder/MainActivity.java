package com.example.bluetoothfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    TextView statusTextView;
    Button searchButton;
    BluetoothAdapter bluetoothAdapter;
    final ArrayList<String> list= new ArrayList<String>();
    final ArrayList<String>  addresses= new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;


    private  final BroadcastReceiver broadcastReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                String action= intent.getAction();
                Log.i("action",action);

                if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                {
                    statusTextView.setText("Finished");
                    searchButton.setEnabled(true);

                }
                else if(BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String name=device.getName();
                    String address= device.getAddress();
                    String rssi= Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
                   Log.i("Device found","Name"+ name + "Address"+ address + "rssi"+ rssi);
                    if(!addresses.contains(address))
                    {
                        addresses.add(address);
                        String deviceString="";
                        if(name==null || name.equals(""))
                        {
                            deviceString=address+ " - RSSI " + rssi + " dBm";

                        }
                        else {
                            deviceString=name+ " - RSSI " + rssi + " dBm";
                        }
                        if(!list.contains(deviceString))
                        {
                            list.add(deviceString);
                            arrayAdapter.notifyDataSetChanged();
                        }

                    }

                     }


        }
    };



    public void searchClicked(View view)
    {
        statusTextView.setText("Searching...");
        searchButton.setEnabled(false);
        addresses.clear();
        list.clear();
        bluetoothAdapter.startDiscovery();



    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
        statusTextView=findViewById(R.id.statusTextView);
        searchButton=findViewById(R.id.searchButton);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(broadcastReceiver,intentFilter);
        arrayAdapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);


    }
}
