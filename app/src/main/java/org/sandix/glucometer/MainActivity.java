package org.sandix.glucometer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    UsbManager mUsbManager;
    ListView deviceList;
    Button refreshBtn, getInfoBtn;
    TextView mMessage;
    UsbDevice usbDevice;
    boolean isConnected=false;

    UsbEndpoint mUsbEndPointOut, mUsbEndPointIn;
    UsbInterface mUsbInterface;
    UsbDeviceConnection mUsbDeviceConnection;

    private byte[] bytes;
    private static int TIMEOUT = 0;
    private static final String ACTION_USB_PERMISSION =
            "org.sandix.glucometer.USB_PERMISSION";
    private static final String HEXES = "0123456789ABCDEF";
    private static final String HEX_INDICATOR = "0x";
    private static final String SPACE = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        deviceList = (ListView) findViewById(R.id.device_list);
        refreshBtn = (Button) findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(this);
        mMessage = (TextView) findViewById(R.id.message);
        getInfoBtn = (Button) findViewById(R.id.get_info);
        getInfoBtn.setOnClickListener(this);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);


        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if(!mUsbManager.hasPermission(usbDevice)){
                //if(action.equals(ACTION_USB_PERMISSION)){

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    mUsbManager.requestPermission(usbDevice, pendingIntent);
                }


                if(mUsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)){
                    mMessage.setText("Device ID: "+usbDevice.getVendorId() + " successfully disconnected");
                    isConnected=false;
                    return;
                }

                if(mUsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)){
                    mMessage.setText("Device ID: "+usbDevice.getVendorId() + " successfully connected");
                    isConnected=true;
                    return;
                }
                //message.setText(intent.toString());
            }

        };
        registerReceiver(broadcastReceiver, filter);
        IntentFilter intentFilter = new IntentFilter("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        IntentFilter intentFilter1 = new IntentFilter("android.hardware.usb.action.USB_DEVICE_DETACHED");
        registerReceiver(broadcastReceiver,intentFilter);
        registerReceiver(broadcastReceiver,intentFilter1);

        //findDevice();
    }

    private void tmp(){
        try {
            List<String> items = new ArrayList<>();
            //Toast.makeText(MainActivity.this, mUsbManager.getDeviceList().values().toString(), Toast.LENGTH_SHORT).show();
            for (UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {

                items.add("DeviceName: "+ usbDevice.getDeviceName().toString());
                items.add("DeviceID: "+ String.valueOf(usbDevice.getDeviceId()));
                items.add("DeviceClass: "+ String.valueOf(usbDevice.getDeviceClass()));
                items.add("DeviceProtocol: "+ String.valueOf(usbDevice.getDeviceProtocol()));
                items.add("DeviceVendorID: "+ String.valueOf(usbDevice.getVendorId()));
                items.add("DeviceProductID: "+ usbDevice.getProductId());
                items.add("DeviceManufName: "+ usbDevice.getManufacturerName());


            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_expandable_list_item_1,items);
            deviceList.setAdapter(adapter);

        }catch (Exception e){
            //Toast.makeText(MainActivity.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
            mMessage.setText(e.getMessage().toString());
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.refresh_btn:
                tmp();
                break;
            case R.id.get_info:
                if(isConnected){
                    communicate();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "No connected glucometer found", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private void communicate() {
        mUsbInterface = usbDevice.getInterface(0);

        for(int nEp=0; nEp<mUsbInterface.getEndpointCount();nEp++){
            UsbEndpoint tmpEndpoint = mUsbInterface.getEndpoint(nEp);
            if(tmpEndpoint.getDirection() == UsbConstants.USB_DIR_IN){
                mUsbEndPointIn = tmpEndpoint;
            }
            if(tmpEndpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                mUsbEndPointOut = tmpEndpoint;
            }
        }

        mUsbDeviceConnection = mUsbManager.openDevice(usbDevice);
        mUsbDeviceConnection.claimInterface(mUsbInterface, true);
        //mMessage.setText("Endpoint IN Direction is: " + mUsbEndPointIn.getDirection() + ", EndPoint OUT Direction is:" + mUsbEndPointOut.getDirection());
       // mUsbDeviceConnection.claimInterface(mUsbInterface, true);
        //byte[] getSerialNum = {0x02};
        byte[] getSerialNum = {0x02,0x09,0x00,0x05,0x0D,0x02,0x03,(byte)0xDA,0x71};
        int num = mUsbDeviceConnection.bulkTransfer(mUsbEndPointOut, getSerialNum,getSerialNum.length,10);
        if(num>0){
            mMessage.setText("num:" +num);
        }


        byte[] buf = new byte[mUsbEndPointIn.getMaxPacketSize()];
        int recv_num = mUsbDeviceConnection.bulkTransfer(mUsbEndPointIn, buf, buf.length,10);
        if(recv_num>0){

        }



        //int num = mUsbDeviceConnection.controlTransfer(UsbConstants.USB_DIR_OUT,0,0,0, getSerialNum, getSerialNum.length,0);

        //byte[] getSerialNum = "mem".getBytes();
        //UsbTreadTx treadTx = new UsbTreadTx(mUsbDeviceConnection,mUsbEndPointIn,this);
        //treadTx.start();
//        mMessage.setText("control Tr: "+ writeData(getSerialNum, getSerialNum.length));
//
//        byte[] buffer = new byte[64];
//        mUsbDeviceConnection.bulkTransfer(mUsbEndPointIn, buffer, mUsbEndPointIn.getMaxPacketSize(), 0);
//        Toast.makeText(this,"Answer: "+hexToString(buffer),Toast.LENGTH_LONG).show();
//
//        byte[] buffer1 = new byte[64];
//        mUsbDeviceConnection.bulkTransfer(mUsbEndPointIn, buffer1, mUsbEndPointIn.getMaxPacketSize(), 0);
//        Toast.makeText(this,"Answer: "+hexToString(buffer1),Toast.LENGTH_LONG).show();
//
//        byte[] buffer2 = new byte[64];
//        mUsbDeviceConnection.bulkTransfer(mUsbEndPointIn, buffer2, mUsbEndPointIn.getMaxPacketSize(), 0);
//        Toast.makeText(this,"Answer: "+hexToString(buffer2),Toast.LENGTH_LONG).show();
       // readData(bytes);



    }

    public static String hexToString(byte[] data)
    {
        if(data != null)
        {
            StringBuilder hex = new StringBuilder(2*data.length);
            for(int i=0;i<=data.length-1;i++)
            {
                byte dataAtIndex = data[i];
                hex.append(HEX_INDICATOR);
                hex.append(HEXES.charAt((dataAtIndex & 0xF0) >> 4))
                        .append(HEXES.charAt((dataAtIndex & 0x0F)));
                hex.append(SPACE);
            }
            return hex.toString();
        }else
        {
            return null;
        }
    }

    public static byte[] stringTobytes(String hexString)
    {
        String stringProcessed = hexString.trim().replaceAll("0x", "");
        stringProcessed = stringProcessed.replaceAll("\\s+","");
        byte[] data = new byte[stringProcessed.length()/2];
        int i = 0;
        int j = 0;
        while(i <= stringProcessed.length()-1)
        {
            byte character = (byte) Integer.parseInt(stringProcessed.substring(i, i+2), 16);
            data[j] = character;
            j++;
            i += 2;
        }
        return data;
    }

    private int readData(byte[] data){
        data = new byte[64];
        int size = Math.min(1, mUsbEndPointIn.getMaxPacketSize());

        return mUsbDeviceConnection.bulkTransfer(mUsbEndPointIn, data, size, 0);

    }

    public int writeData(final byte[] data, final int length) {
        int offset = 0;

        while (offset < length) {
            int size = Math.min(length - offset, mUsbEndPointIn.getMaxPacketSize());
            int bytesWritten = mUsbDeviceConnection.bulkTransfer(mUsbEndPointOut,
                    Arrays.copyOfRange(data, offset, offset + size), size, 0);

           // if (bytesWritten <= 0)
            offset += bytesWritten;
        }
        return offset;
    }

}
