package com.hofo.bluetoothservice.bluetoothleclientdemo.util;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.RequiresPermission;

public class BluetoothController {
    private static volatile BluetoothController singleton;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean isEnable;

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    private BluetoothController() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        isEnable = mBluetoothAdapter != null;
    }

    /**
     * 获得蓝牙控制器对象
     *
     * @return
     */
    public static BluetoothController getInstance() {
        if (singleton == null) {
            synchronized (BluetoothController.class) {
                if (singleton == null) {
                    singleton = new BluetoothController();
                }
            }
        }
        return singleton;
    }

    /**
     * 打开蓝牙
     */
    public void openBluetooth(Context mContext) {
        if (isEnable && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.getApplicationContext().startActivity(enableBtIntent);
        }
    }
    /**
     * 不需要提示并打开蓝牙
     */
    public void openBluetoothWithNoHint() {
        if (isEnable && !mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }


/**
 * 开始蓝牙扫描
 *
 * @param scanCallback
 */
@RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
public void scanLeDevice(ScanCallback scanCallback) {
    if (isEnable && mBluetoothAdapter.isEnabled()) {
        BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.startScan(scanCallback);
    }
}

/**
 * 停止蓝牙扫描
 *
 * @param scanCallback
 */
@RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
public void stopScanLeDevice(ScanCallback scanCallback) {
    if (isEnable && mBluetoothAdapter.isEnabled()) {
        BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.stopScan(scanCallback);
    }
}


}
