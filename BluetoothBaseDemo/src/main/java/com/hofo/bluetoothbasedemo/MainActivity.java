package com.hofo.bluetoothbasedemo;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hofo.bluetoothbasedemo.util.BluetoothController;
import com.hofo.bluetoothbasedemo.util.L;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private BluetoothController mController = BluetoothController.getInstance();
    private int REQUEST_ENABLE_BT = 1;
    private String TAG = "AppDebug";
    private ArrayAdapter<String> adapter;
    private BroadcastReceiver bluetoothChangeBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            int pstate = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0);
            L.e("新的蓝牙状态 =" + state + "\t旧的蓝牙状态 =" + pstate);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    L.e("蓝牙已关闭");
                    break;
                case BluetoothAdapter.STATE_ON:
                    L.e("蓝牙已打开");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    L.e("正在打开蓝牙");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    L.e("正在关闭蓝牙");
                    break;
                default:
                    L.e("未知状态" + state);
            }


            String action = intent.getAction();
            //找到设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                adapter.add(scanDevice.getName() + "\t->\t" + scanDevice.getAddress());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                L.e("搜索完成");
            }
            adapter.notifyDataSetChanged();
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 1);
            switch (mode) {
                case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                    L.e("可检测到模式");
                    break;
                case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                    L.e("未处于可检测到模式但仍能接收连接");
                case BluetoothAdapter.SCAN_MODE_NONE:
                    L.e("未处于可检测到模式并且无法接收连接");

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestAllPower();
        IntentFilter bluetoothChange = new IntentFilter();
        //设备连接状态改变
        bluetoothChange.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //蓝牙设备状态改变
        bluetoothChange.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //发现设备
        bluetoothChange.addAction(BluetoothDevice.ACTION_FOUND);


        registerReceiver(bluetoothChangeBR, bluetoothChange);
        ListView lv = findViewById(R.id.lv);
        ArrayList data = new ArrayList();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        lv.setAdapter(adapter);

        //注册广播，监听模式改变
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mReceiver, filter1);
    }

    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

    public void isSupport(View view) {
        L.e(mController.isSupport() ? "支持蓝牙" : "不支持蓝牙");
    }

    public void isOpen(View view) {
        L.e(mController.isOpen() ? "蓝牙已打开" : "蓝牙已关闭");
    }

    /**
     * 打开蓝牙
     *
     * @param view
     */
    public void openBluetooth(View view) {
        mController.openBluetooth(this, REQUEST_ENABLE_BT);
    }

    public void closeBluetooth(View view) {
        mController.closeBluetooth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_ENABLE_BT == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                L.e("蓝牙已打开");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                L.e("用户拒绝");
            }
        }
    }

    public void pair(View view) {
        adapter.clear();
        Set<BluetoothDevice> pairedDevices = mController.getPair();
        // 配对设备
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                // 添加到ListView
                adapter.add(device.getName() + "->" + device.getAddress());
            }
        }
    }

    public void search(View view) {
        adapter.clear();

        mController.searchDevices(this);
    }

    public void stopSearch(View view) {
        mController.stopSearch();
    }

    public void visible(View view) {
        mController.setVisible(this, 0);
    }
}
