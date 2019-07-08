package com.hofo.baseblesetpin;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hofo.baseblesetpin.util.BluetoothController;
import com.hofo.baseblesetpin.util.LAbsAdapter;

import java.util.HashSet;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    HashSet<String> macs = new HashSet<>();
    private ListView mLV;
    private BluetoothController mBluetoothController;
    private Context mContext;
    private DevicesAdapter mDevicesAdapter;
    private BroadcastReceiver bluetoothChangeBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (macs.add(scanDevice.getAddress())) {
                    mDevicesAdapter.addElement(scanDevice);
                    mDevicesAdapter.notifyDataSetChanged();
                }
            }

        }
    };
    private ConnectThread mConnectThread;
    private BluetoothDevice mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothController = BluetoothController.getInstance();
        mContext = this;
        requestAllPower();
        mLV = findViewById(R.id.LV);
        mDevicesAdapter = new DevicesAdapter(this);
        mLV.setAdapter(mDevicesAdapter);
        mLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDevice = mDevicesAdapter.getItem(position);
                mConnectThread = new ConnectThread(mDevice);
                mConnectThread.start();
            }
        });

        IntentFilter bluetoothChange = new IntentFilter();
        //发现设备
        bluetoothChange.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothChangeBR, bluetoothChange);
    }

    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    public void del(View view) {
        mBluetoothController.unpairDevice(mDevice);
    }

    public void scan(View view) {
        mDevicesAdapter.removeAll();
        mDevicesAdapter.notifyDataSetChanged();
        mBluetoothController.searchDevices(mContext);
    }

    class ConnectThread extends Thread {
        BluetoothDevice mBluetoothDevice;

        public ConnectThread(BluetoothDevice bluetoothDevice) {
            mBluetoothDevice = bluetoothDevice;
        }

        @Override
        public void run() {
            super.run();
            try {
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                BluetoothSocket socket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                socket.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class DevicesAdapter extends LAbsAdapter<BluetoothDevice> {

        public DevicesAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        public void bindData(int position, BluetoothDevice bean, ViewHolder holder) {
            holder.setText(android.R.id.text1, bean.getName() + "\n" + bean.getAddress());
        }

        @Override
        public int getLayoutId() {
            return android.R.layout.simple_list_item_1;
        }
    }
}
