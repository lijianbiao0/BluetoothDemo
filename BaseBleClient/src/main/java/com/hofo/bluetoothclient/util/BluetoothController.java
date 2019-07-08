package com.hofo.bluetoothclient.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import java.util.Set;

public class BluetoothController {
    private static BluetoothController mController = new BluetoothController();
    private BluetoothAdapter mAdapter;

    private BluetoothController() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothController getInstance() {
        return mController;
    }

    public boolean isSupport() {
        return mAdapter != null;
    }

    public boolean isOpen() {
        if (mAdapter != null) {
            return mAdapter.isEnabled();
        }
        return false;
    }

    public void openBluetooth(Activity activity, int requestCode) {
        if (mAdapter != null && !mAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, requestCode);
//            mAdapter.enable();//可能失败，不提示直接开启蓝牙设备
        }

    }

    public void closeBluetooth() {
        if (mAdapter != null && mAdapter.isEnabled()) {
            mAdapter.disable();
        }
    }

    public Set<BluetoothDevice> getPair() {
        Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
        return pairedDevices;
    }

    public BluetoothAdapter getAdapter() {
        return mAdapter;
    }

    public void searchDevices(Context mContext) {
        if (mAdapter != null && mAdapter.isEnabled()) {
            if(isGpsEnable(mContext)){
                mAdapter.startDiscovery();
            }else {
                //跳转到gps设置页
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);

//                mContext.startActivityForResult(intent,requestCode);
            }

        }
    }
    // gps是否可用
    public static final boolean isGpsEnable(Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    public void stopSearch() {
        if (mAdapter != null && mAdapter.isEnabled()&&mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }

    }

    /**
     * 默认情况下，设备将变为可检测到并持续 120 秒钟。
     * 应用可以设置的最大持续时间为 3600 秒，值为 0 则表示设备始终可检测到。
     * 任何小于 0 或大于 3600 的值都会自动设为 120 秒。
     * @param activity
     * @param time
     */
    public void setVisible(Activity activity,int time) {
        if (mAdapter != null && mAdapter.isEnabled()) {
            Intent discoverableIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
            activity.startActivity(discoverableIntent);
        }
    }


}
