package com.example.b8uetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ledcontrolclass extends Activity {


//    LedControl(String addr){
//        this.address=addr;
//    }


    String address = null;


    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    Thread thread;
    byte buffer[];
    int bufferPosition;
    boolean stopThread;
    private InputStream inputStream;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public void ledcontrolfuntion(String addrrr) {
        new ConnectBT().execute();
        address=addrrr;
//        ConnectBT cbt=new ConnectBT();
//        cbt.execute();
    }




        public class ConnectBT extends AsyncTask<Void, Void, Void> {
            private boolean ConnectSuccess = true;


            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(Void... devices) {
                try {
                    if (btSocket == null || !isBtConnected) {
                        myBluetooth = BluetoothAdapter.getDefaultAdapter();
                        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        btSocket.connect();

//                    my added code
                        if (btSocket != null) {
                            // beginListenForData();
                        }
                    }
                } catch (IOException e) {
                    ConnectSuccess = false;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                if (!ConnectSuccess) {
                    msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                    finish();
                } else {
                    msg("Connected");
                    isBtConnected = true;
                }
                beginListenForData();

            }


        }


        void beginListenForData ()
        {

            try {
                inputStream = btSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            final Handler handler = new Handler();
            stopThread = false;
            buffer = new byte[1024];


            Thread thread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopThread) {
                        try {
                            int byteCount = inputStream.available();
                            if (byteCount > 0) {
                                byte[] rawBytes = new byte[byteCount];
                                inputStream.read(rawBytes);
                                final String string = new String(rawBytes, "UTF-8");
                                handler.post(new Runnable() {
                                    public void run() {

                                        System.out.println(string);
                                        if (string!="R") {
                                            System.out.print("this is R");
                                            Intent i = new Intent(ledcontrolclass.this, AlertActivity.class);
                                            startActivity(i);
                                        }
                                    }
                                });

                            }
                        } catch (IOException ex) {
                            stopThread = true;
                        }
                    }
                }
            });

            thread.start();
        }


        private void Disconnect () {
            if (btSocket != null) {
                try {
                    btSocket.close();
                } catch (IOException e) {
                    msg("Error");
                }
            }

            finish();
        }


        private void msg (String s){
//            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }


    }


