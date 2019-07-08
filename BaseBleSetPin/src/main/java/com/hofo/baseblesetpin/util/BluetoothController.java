package com.hofo.baseblesetpin.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Set;

public class BluetoothController {
    private static BluetoothController mController = new BluetoothController();
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothController() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothController getInstance() {
        return mController;
    }

    public boolean isSupport() {
        return mBluetoothAdapter != null;
    }

    public boolean isOpen() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.isEnabled();
        }
        return false;
    }

    public void openBluetooth(Activity activity, int requestCode) {

        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, requestCode);
//            mBluetoothAdapter.enable();//可能失败，不提示直接开启蓝牙设备
        }

    }

    public void closeBluetooth() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    public Set<BluetoothDevice> getPair() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        return pairedDevices;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void searchDevices(Context mContext) {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {

            if (isGpsEnable(mContext)) {
                mBluetoothAdapter.startDiscovery();
            } else {
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
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

    }

    /**
     * 默认情况下，设备将变为可检测到并持续 120 秒钟。
     * 应用可以设置的最大持续时间为 3600 秒，值为 0 则表示设备始终可检测到。
     * 任何小于 0 或大于 3600 的值都会自动设为 120 秒。
     *
     * @param activity
     * @param time
     */
    public void setVisible(Activity activity, int time) {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            Intent discoverableIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
            activity.startActivity(discoverableIntent);
        }
    }

    //得到配对的设备列表，清除已配对的设备
    public void removePairDevice() {
        if (mBluetoothAdapter != null) {
            //mBluetoothAdapter初始化方式 mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            //这个就是获取已配对蓝牙列表的方法
            Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : bondedDevices) {
                //这里可以通过device.getName()  device.getAddress()来判断是否是自己需要断开的设备
                unpairDevice(device);
            }
        }
    }

    /**
     * 删除已配对设备
     *
     * @param device
     */
    public void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e("mate", e.getMessage());
        }
    }


}
