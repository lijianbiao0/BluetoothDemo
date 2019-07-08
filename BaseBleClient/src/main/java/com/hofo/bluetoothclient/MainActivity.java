package com.hofo.bluetoothclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hofo.bluetoothclient.util.BluetoothController;
import com.hofo.bluetoothclient.util.L;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final UUID MY_UUID = UUID.fromString("51c49858-7f4b-4df9-a355-dea73e2cb316");
    private ArrayAdapter adapter;
    private BluetoothController mController;
    private ConnectedThread connectedThread;

    private BroadcastReceiver bluetoothChangeBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //每扫描到一个设备，系统都会发送 BluetoothDevice.ACTION_FOUND 广播。
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if ("ANDROID BT".equals(scanDevice.getName())) {
                    connectedThread = new ConnectedThread(scanDevice);
                    connectedThread.start();
                    Toast.makeText(context, "连接目标设备", Toast.LENGTH_SHORT).show();
                }

                adapter.add(scanDevice.getName() + "\n" + scanDevice.getAddress());
            }
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mController = BluetoothController.getInstance();

        ListView listView = findViewById(R.id.lv);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        IntentFilter bluetoothChange = new IntentFilter();
        //发现设备
        bluetoothChange.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothChangeBR, bluetoothChange);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothChangeBR != null) {
            unregisterReceiver(bluetoothChangeBR);
        }
    }

    public void search(View view) {
        adapter.clear();
        mController.searchDevices(this);
    }

    public void stop(View view) {
        mController.stopSearch();
    }

    public void send(View view) {
        connectedThread.write("你好");
    }

    private class ConnectedThread extends Thread {
        private InputStream mInputStream;
        private OutputStream mOutputStream;
        private BluetoothSocket mBluetoothSocket;


        public ConnectedThread(BluetoothDevice scanDevice) {
            try {
                mBluetoothSocket = scanDevice.createRfcommSocketToServiceRecord(MY_UUID);

                mBluetoothSocket.connect();

                mInputStream = mBluetoothSocket.getInputStream();
                mOutputStream = mBluetoothSocket.getOutputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[1024];
                int len;

                while ((len = mInputStream.read(buffer)) != -1) {
                    String msg = new String(buffer, 0, len);
                    L.e("数据：" + msg);
                }
            } catch (IOException e) {
            }
        }


        public void write(String str) {
            try {
                mOutputStream.write(str.getBytes());
                mOutputStream.flush();
            } catch (IOException e) {
            }
        }
    }
}
