package com.hofo.bluetoothleclientdemo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hofo.bluetoothleclientdemo.util.BluetoothController;
import com.hofo.bluetoothleclientdemo.util.L;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private BluetoothController mBluetoothController;
    private HashSet mDevices = new HashSet();
    private BluetoothGatt mBluetoothGatt;
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (mDevices.add(result.getDevice().getAddress())) {

                BluetoothDevice device = result.getDevice();
                L.e("蓝牙设备:" + device.getName() + "->" + device.getAddress());
                if ("08:56:87:06:26:10".equals(device.getAddress())) {
                    mBluetoothGatt = device.connectGatt(mContext, true, mBluetoothGattCallback);
                }

            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            L.e("批次扫描结果-onBatchScanResults");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            L.e("扫描失败：onScanFailed-errorCode:" + errorCode);
        }
    };
    private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            L.e("===========\t连接状态改变\t===========");
            if (newState == BluetoothProfile.STATE_CONNECTING) {
                L.e("-----------\t连接中....\t-----------");
            } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                L.e("连接到GATT服务器\n启动搜索GATT服务器存在的服务:" +
                        (mBluetoothGatt.discoverServices() ? "启动成功" : "启动失败"));
//                调用discoverServices函数会触发onServicesDiscovered函数
                //停止扫描
                mBluetoothController.stopScanLeDevice(mScanCallback);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED || newState == BluetoothProfile.STATE_DISCONNECTING) {
                L.e("与GATT服务器断开连接.");

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            //发现了新服务
            if (status == BluetoothGatt.GATT_SUCCESS) {
                displayGattServices(gatt);
            } else {
                L.e("onServicesDiscovered received: " + status);
            }
        }

        /**
         * 显示新服务
         * @param gatt
         */
        private void displayGattServices(BluetoothGatt gatt) {
            List<BluetoothGattService> gattServices = gatt.getServices();
            if (gattServices == null) return;

            for (BluetoothGattService gattService : gattServices) {
                List<BluetoothGattCharacteristic> characteristics = gattService.getCharacteristics();

                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : characteristics) {
                    if (bluetoothGattCharacteristic.getUuid().equals(UUID.fromString("0000fee1-0000-1000-8000-00805f9b34fb"))) {
                        mBluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
                        BluetoothGattDescriptor descriptor = bluetoothGattCharacteristic.getDescriptor(
                                bluetoothGattCharacteristic.getUuid());
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                            mBluetoothGatt.writeDescriptor(descriptor);
                        }
                    }
                }
            }
        }

        @Override
        // 特征读取操作的结果
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
        }

        /**
         * 特征值改变
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] data = characteristic.getValue();
            StringBuilder datasb = new StringBuilder();
            if (data != null && data.length > 0) {
                for (byte byteChar : data)
                    datasb.append(String.format("%02X ", byteChar));
                L.e("称端数据:" + datasb.toString());
            }
            String[] dataSplit = datasb.toString().split(" ");
            String weightHex = "";
            if (dataSplit[0].equals("0A")) {
                if (!dataSplit[3].equals("00")) {
                    weightHex += dataSplit[3];
                }
                if (!dataSplit[2].equals("00")) {
                    weightHex += dataSplit[2];
                }
                if (!dataSplit[1].equals("00")) {
                    weightHex += dataSplit[1];
                }
                int weight = Integer.parseInt(weightHex, 16);
                L.e("秤端稳定十六进制:" + weightHex);
                L.e("秤端稳定十进制:" + weight);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mBluetoothController = BluetoothController.getInstance();
    }

    @Override
    protected void onDestroy() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        super.onDestroy();
    }

    public void openBluetooth(View view) {
        mBluetoothController.openBluetoothWithNoHint();
    }

    public void scanBluetooth(View view) {
        mBluetoothController.scanLeDevice(mScanCallback);
    }

    public void stopScanBluetooth(View view) {
        mBluetoothController.stopScanLeDevice(mScanCallback);
    }
}