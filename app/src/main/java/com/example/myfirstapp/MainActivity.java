package com.example.myfirstapp;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import java.lang.Object;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.UUID;

import java.security.PrivateKey;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Set;
import android.widget.Toast;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.UUID;
import java.util.logging.Handler;
//https://stuff.mit.edu/afs/sipb/project/android/docs/guide/topics/connectivity/bluetooth.html#top

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ArrayList list=new ArrayList();
    BluetoothAdapter mBluetoothAdapter;
    private static final UUID MY_UUID=UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private ConnectThread mConnectedThread;
    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            BluetoothDevice mBondDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(mBondDevice.getBondState()==BluetoothDevice.BOND_BONDED){
                Toast.makeText(getApplicationContext(), "Device bonded.",
                        Toast.LENGTH_SHORT).show();
            }
            if(mBondDevice.getBondState()==BluetoothDevice.BOND_BONDING){
                Toast.makeText(getApplicationContext(), "Device bonding.",
                        Toast.LENGTH_SHORT).show();
            }
            if(mBondDevice.getBondState()==BluetoothDevice.BOND_NONE){
                Toast.makeText(getApplicationContext(), "Device not bonded.",
                        Toast.LENGTH_SHORT).show();
            }
        }

        };

            // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            ArrayList<String> mDeviceList = new ArrayList<String>();
            ListView listView;
            listView=(ListView)findViewById(R.id.listview);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName() + "\n" + device.getAddress());
                listView.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, mDeviceList));
                Toast.makeText(getApplicationContext(), "Fetching device list.",
                        Toast.LENGTH_SHORT).show();
            }
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,mBluetoothAdapter.ERROR);
                Set<BluetoothDevice> pairedDevices23 = mBluetoothAdapter.getBondedDevices();
                TextView farhan =(TextView)findViewById(R.id.textView2);
                switch (state){

                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        farhan.setText("Disabled Bluetooth");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: State Turning OFF");
                        farhan.setText("Bluetooth Turning OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: State ON");
                        farhan.setText("Bluetooth is Enabled");
                        if (pairedDevices23.size() > 0) {
                            // There are paired devices. Get the name and address of each paired device.
                            for (BluetoothDevice device : pairedDevices23) {
                                String deviceName = device.getName();
                                String deviceHardwareAddress = device.getAddress(); // MAC address
                                TextView farhan24=(TextView)findViewById(R.id.textView2);
                                farhan24.setText(String.valueOf(deviceName+'\n'+deviceHardwareAddress));
                                Toast.makeText(getApplicationContext(), "pair devices found.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: State Turning ON");
                        break;
                }
            }
        }
    };


    @Override
    protected void onDestroy(){
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
    }

    public static class MyBluetoothService {
        private static final String TAG = "MY_APP_DEBUG_TAG";
        private Handler mHandler; // handler that gets info from Bluetooth service

        // Defines several constants used when transmitting messages between the
        // service and the UI.
        private interface MessageConstants {
            public static final int MESSAGE_READ = 0;
            public static final int MESSAGE_WRITE = 1;
            public static final int MESSAGE_TOAST = 2;

            // ... (Add other message types here as needed.)
        }

        private class ConnectedThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            private final OutputStream mmOutStream;
            private byte[] mmBuffer; // mmBuffer store for the stream

            public ConnectedThread(BluetoothSocket socket) {

                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                // Get the input and output streams; using temp objects because
                // member streams are final.
                try {
                    tmpIn = socket.getInputStream();
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when creating input stream", e);
                }
                try {
                    tmpOut = socket.getOutputStream();
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when creating output stream", e);
                }

                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }

            public void run() {
                mmBuffer = new byte[1024];
                int numBytes; // bytes returned from read()

                // Keep listening to the InputStream until an exception occurs.
                while (true) {
                    try {
                        // Read from the InputStream.
                        numBytes = mmInStream.read(mmBuffer);
                        // Send the obtained bytes to the UI activity.                        
//                        Message readMsg = mHandler.obtainMessage(
//                                MessageConstants.MESSAGE_READ, numBytes, -1,
//                                mmBuffer);
                        Message readMsg=Message.obtain();
                        readMsg.sendToTarget();

                    } catch (IOException e) {
                        Log.d(TAG, "Input stream was disconnected", e);
                        break;
                    }
                }
            }

            // Call this from the main activity to send data to the remote device.
            public void write(byte[] bytes) {
                try {
                    mmOutStream.write(bytes);

                    // Share the sent message with the UI activity.
//                    Message writtenMsg = mHandler.obtainMessage(
//                            MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                    Message writtenMsg=Message.obtain();
                    writtenMsg.sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when sending data", e);

                    // Send a failure message back to the activity.
//                    Message writeErrorMsg =
//                            mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("toast",
//                            "Couldn't send data to the other device");
//                    writeErrorMsg.setData(bundle);
//                    mHandler.sendMessage(writeErrorMsg);
                }
            }


            // Call this method from the main activity to shut down the connection.
            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close the connect socket", e);
                }
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66"));
                Toast.makeText(getApplicationContext(), "first loop of SOCKET found.",Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            Toast.makeText(getApplicationContext(), "SOCKET found.",Toast.LENGTH_SHORT).show();
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Toast.makeText(getApplicationContext(), "TRIED.",
                        Toast.LENGTH_SHORT).show();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                    Toast.makeText(getApplicationContext(), "Could not close the client socket.",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
          //  manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }


    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                Toast.makeText(getApplicationContext(), "FIrst loop of Adapter created.",
                        Toast.LENGTH_SHORT).show();
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("My First App", UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66"));
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
            Toast.makeText(getApplicationContext(), "Adapter created.",
                    Toast.LENGTH_SHORT).show();
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                    Toast.makeText(getApplicationContext(), "server side socket found.",
                            Toast.LENGTH_SHORT).show();
                    TextView farhan24=(TextView)findViewById(R.id.textView2);
                    farhan24.setText("socket established");
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    //new MyBluetoothService.ConnectedThread(socket);
                    Toast.makeText(getApplicationContext(), "go to connected thread.",
                            Toast.LENGTH_SHORT).show();
                     //manageMyConnectedSocket(socket);
                    //mmServerSocket.close();
                    TextView farhan24=(TextView)findViewById(R.id.textView2);
                    farhan24.setText("no socket established");
                    break;
                }
            }
        }



        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
//    public void startclient(BluetoothDevice device,UUID uuid){
//        mConnectThread=new ConnectThread(device,UUID);
//        mConnectThread.start();
//    }


//    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice){
//        mConnectedThread = new ConnectThread(mmSocket);
//        mConnectedThread.start();
//    }

//    public void write(byte[] out){
//        MyBluetoothService.ConnectedThread r;
//        mConnectedThread.write(out);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnonoff = (Button) findViewById(R.id.btnonoff);
        Button dlist = (Button) findViewById(R.id.dlist);
        final Button server = (Button)findViewById(R.id.server);
        Button client = (Button)findViewById(R.id.client);
        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "server clicked.",
                        Toast.LENGTH_SHORT).show();
                if(mBluetoothAdapter.isEnabled()){
//                    Thread t1=new Thread(new AcceptThread());
//                    t1.run();
                AcceptThread AcceptThread=new AcceptThread();
                AcceptThread.start();
                    //public InputStream getInputStream ();

                //MyBluetoothService.ConnectedThread(BluetoothServerSocket getInputStream());

//                try {
//                    AcceptThread.mmServerSocket.accept();
//                    Toast.makeText(getApplicationContext(), "socket started",
//                            Toast.LENGTH_SHORT).show();
//                }catch (IOException e){
//                    Toast.makeText(getApplicationContext(), "exception occurs.",
//                            Toast.LENGTH_SHORT).show();
//                }
                }
                else{
                    Toast.makeText(getApplicationContext(), "First Enable Bluetooth.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "client clicked.",
                        Toast.LENGTH_SHORT).show();
                if(mBluetoothAdapter.isEnabled()){
                    ConnectThread ConnectThread=new ConnectThread(mBluetoothAdapter.getRemoteDevice("AC:5A:14:38:D6:2C"));
                    ConnectThread.start();
                }
                else{
                    Toast.makeText(getApplicationContext(), "First Enable Bluetooth.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        dlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Fetching device list.",
//                        Toast.LENGTH_SHORT).show();
//                TextView farhan=(TextView)findViewById(R.id.textView2);
//                farhan.setText("Saeed");

                Intent listActivityIntent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(listActivityIntent);
            }
        });
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();
        IntentFilter bond=new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver2,bond);
        IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver1,filter);
        btnonoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                Toast.makeText(getApplicationContext(), "enable/disable button clicked.",
                        Toast.LENGTH_SHORT).show();
                enableDisableBT();
            }
        });
    }

    public void enableDisableBT(){

        if(mBluetoothAdapter == null){
            Log.d(TAG,"enableDisableBT: Does not have BT capabilities");
            Toast.makeText(getApplicationContext(), "device does not have bluetooth.",
                        Toast.LENGTH_SHORT).show();
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            Toast.makeText(getApplicationContext(), "getting bonded devices.",
                    Toast.LENGTH_SHORT).show();

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    Toast.makeText(getApplicationContext(), "pair devices found.",
                            Toast.LENGTH_SHORT).show();
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    TextView farhan24=(TextView)findViewById(R.id.textView2);
                    farhan24.setText(String.valueOf(deviceName+deviceHardwareAddress));
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "No pair devices found.",
                        Toast.LENGTH_SHORT).show();
            }

            ArrayAdapter mpairedDevicesArray;
            mpairedDevicesArray = new ArrayAdapter(this,R.layout.activity_main);
            for(BluetoothDevice device:pairedDevices){

                //mpairedDevicesArray = new ArrayAdapter(this,R.layout.activity_main);
                mpairedDevicesArray.add(pairedDevices);
                ListView farhan23=(ListView)findViewById(R.id.listview);
                farhan23.setAdapter(mpairedDevicesArray);
            }

            TextView farhan=(TextView)findViewById(R.id.textView2);
            farhan.setText(String.valueOf(mpairedDevicesArray));
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

}