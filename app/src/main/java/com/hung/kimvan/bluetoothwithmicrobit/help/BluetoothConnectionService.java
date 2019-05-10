package com.hung.kimvan.bluetoothwithmicrobit.help;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {
    // Debugging
    private static final String TAG = "BluetoothChatService";

    private static final String appName = "BluetoothWithMycrobit";

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private BluetoothAdapter bluetoothAdapter;
    Context mContext;
    private AcceptThread mInsecureAcceptThread;

    private ConnectThread connectThread;
    private BluetoothDevice bluetoothDevice;
    private UUID deviceUUID;
    ProgressDialog progressDialog;

    private ConnectedThread connectedThread;


    public BluetoothConnectionService(Context mContext) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mContext = mContext;
        start();
    }

    private class AcceptThread extends Thread{
        private BluetoothServerSocket bluetoothServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            //create a listening server socket
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName,MY_UUID_INSECURE);
            }catch (IOException ex){

            }

            bluetoothServerSocket = tmp;


        }

        @Override
        public void run() {
            BluetoothSocket socket = null;
            try {
                socket = bluetoothServerSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (socket != null){
                connected(socket,bluetoothDevice);
            }
        }

        public void cancel(){
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectThread extends Thread{
        private BluetoothSocket bluetoothSocket;

        public ConnectThread(BluetoothDevice device,UUID uuid) {
            Log.d(TAG,"ConnectThread started");
            bluetoothDevice = device;
            deviceUUID = uuid;
        }

        @Override
        public void run() {
            BluetoothSocket tmp = null;
            try {
                tmp = bluetoothDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            bluetoothSocket = tmp;

            bluetoothAdapter.cancelDiscovery();

            try {
                bluetoothSocket.connect();
                Log.d(TAG,"Connected");
            } catch (IOException e) {
                try {
                    bluetoothSocket.close();
                    Log.d(TAG,"Closed socket");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }

            connected(bluetoothSocket,bluetoothDevice);
        }

        public void cancel(){
            try {
                bluetoothSocket.close();
                Log.d(TAG,"Close success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void start(){
        Log.d(TAG,"Start synchronized");

        if(connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }

        if (mInsecureAcceptThread == null){
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }


    }

    public void startClient(BluetoothDevice device,UUID uuid){
        Log.d(TAG,"StartClient: Started");

        progressDialog = ProgressDialog.show(mContext,"Connecting Bluetooth",
                "Please wait...",true);

        connectThread = new ConnectThread(device,uuid);
        connectThread.start();
    }

    private class ConnectedThread  extends Thread{
        private BluetoothSocket bluetoothSocket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            Log.d(TAG,"ConnectedThread: Starting");
            this.bluetoothSocket = bluetoothSocket;

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //dismiss when connection is established
                progressDialog.dismiss();
            }catch (NullPointerException e){

            }

            try {
                tmpIn = bluetoothSocket.getInputStream();
                tmpOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tmpIn;
            outputStream = tmpOut;

        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];

            int bytes ;

            //keep listening to the InputStream until an exception occurs
            while (true){
                try {
                    bytes = inputStream.read(buffer);
                    String imcomingMessage = new String(buffer,0,bytes);
                    Log.d(TAG,"Message incoming: "+imcomingMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"Error read input bytes:"+e.getMessage());
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            String text = new String(bytes,Charset.defaultCharset());
            Log.d(TAG,"Write byte: "+text);
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"Error writing bytes:"+e.getMessage());
            }
        }

        public void cancel(){
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void connected(BluetoothSocket socket,BluetoothDevice device){
        Log.d(TAG,"in Connected function");

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    public void write(byte[] out){
        Log.d(TAG,"on write function");
        connectedThread.write(out);
    }
}
