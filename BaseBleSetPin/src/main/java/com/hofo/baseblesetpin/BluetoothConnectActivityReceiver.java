package com.hofo.baseblesetpin;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hofo.baseblesetpin.util.ClsUtils;

public class BluetoothConnectActivityReceiver extends BroadcastReceiver {

@Override
public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
        BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        try {
            //(三星)4.3版本测试手机还是会弹出用户交互页面(闪一下)，如果不注释掉下面这句
            // 页面不会取消但可以配对成功。(中兴，魅族4(Flyme 6))5.1版本手机两中情况下都正常
            //ClsUtils.setPairingConfirmation(mBluetoothDevice.getClass(), mBluetoothDevice, true);
            abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
            //3.调用setPin方法进行配对...
            boolean ret = ClsUtils.setPin(mBluetoothDevice.getClass(), mBluetoothDevice, "0000");
            Toast.makeText(context, ret ? "配对成功" : "配对失败", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
}
