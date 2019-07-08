package com.hofo.bluetoothservice;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hofo.bluetoothservice.bluetoothleclientdemo.util.BluetoothController;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AcceptThread().start();
    }

    private class AcceptThread extends Thread {
        private UUID MY_UUID = UUID.fromString("51c49858-7f4b-4df9-a355-dea73e2cb316");
        private String NAME = "Bluetooth";
        private BluetoothServerSocket mServerSocket;

        public AcceptThread() {
            try {
                mServerSocket = BluetoothController.getInstance()
                        .getBluetoothAdapter().listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
            }
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = mServerSocket.accept();
                    if (socket != null) {
                        manageConnectedSocket(socket);
                        mServerSocket.close();
                        break;
                    }
                } catch (IOException e) {
                    break;
                }

            }
        }

        private void manageConnectedSocket(final BluetoothSocket socket) throws IOException {
            socket.getOutputStream().write("你好啊！".getBytes());

            InputStream is = socket.getInputStream();
            byte[] b = new byte[1024];
            int len;
            while ((len = is.read(b)) != -1) {
                String msg = new String(b, 0, len);
                Log.e("Appdebug", msg);
            }
        }
    }
}
