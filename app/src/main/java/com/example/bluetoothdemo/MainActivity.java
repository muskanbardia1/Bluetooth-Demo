package com.example.bluetoothdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.lv);

        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        listView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

//IntentFilter stateFilter = new IntentFilter();
//stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        IntentFilter stateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(stateReceiver, stateFilter);

        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(foundReceiver,foundFilter);
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(stateReceiver);
        unregisterReceiver(foundReceiver);
    }


    BroadcastReceiver foundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND))
            {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String name = bluetoothDevice.getName();
                String macAddress = bluetoothDevice.getAddress();

                Toast.makeText(context, name +" "+ macAddress, Toast.LENGTH_SHORT).show();
            }
        }
    };

    BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
            {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
                switch (state)
                {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(context, "Bluetooth is turning on", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(context, "Bluetooth is on", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(context, "Bluetooth is turning off", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(context, "Bluetooth is off", Toast.LENGTH_SHORT).show();
                        break;
                }

            }

        }
    };

    public void enableBluetooth(View view)
    {
        if (bluetoothAdapter != null)
        {
            Toast.makeText(this, "Device Supports Bluetooth", Toast.LENGTH_SHORT).show();
            if (! bluetoothAdapter.isEnabled())
            {
//Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                Intent i = new Intent();
                i.setAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i, 1);

            }
        }
        else
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(this, "Bluetooth Turned On", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "Bluetooth on request canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void disableBluetooth(View view)
    {
        if (bluetoothAdapter != null)
        {
            if (bluetoothAdapter.isEnabled())
            {
                if(bluetoothAdapter.disable())
                    Toast.makeText(this, "Bluetooth Turned off", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getPairedDevices(View view)
    {
        if(bluetoothAdapter != null)
        {
            if (bluetoothAdapter.isEnabled())
            {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if(pairedDevices.size() >0)
                {
                    for(BluetoothDevice device : pairedDevices)
                    {
                        String name = device.getName();
                        String macAddress = device.getAddress();

                        arrayList.add(name+" "+macAddress);
                        Toast.makeText(this, name+ " "+ macAddress, Toast.LENGTH_SHORT).show();
                    }

                    arrayAdapter.notifyDataSetChanged();

                }
            }

        }
    }

    void checkLocationPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            flag = true;
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
//super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode ==2)
        {
            if (grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                flag = true;
            else
                flag = false;
        }

    }

    public void discoverBluetoothDevices(View view)
    {
        checkLocationPermission();

        if (bluetoothAdapter != null)
        {
            if (bluetoothAdapter.isEnabled())
            {
                if (flag)
                {
                    if (bluetoothAdapter.startDiscovery())
                        Toast.makeText(this, "Start Discovery Successfully", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(this, "Start Discovery failed", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}