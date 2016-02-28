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

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if(action.equals(ACTION_USB_PERMISSION)){

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
            if(tmpEndpoint.getDirection() == UsbConstants.USB_DIR_OUT){
                mUsbEndPointOut = tmpEndpoint;
            }
        }

        mUsbDeviceConnection = mUsbManager.openDevice(usbDevice);
        mMessage.setText("Endpoint IN Direction is: " + mUsbEndPointIn.getDirection() + ", EndPoint OUT Direction is:" + mUsbEndPointOut.getDirection());
        mUsbDeviceConnection.claimInterface(mUsbInterface, true);
        byte[] b = {0x02,0x12,0x00,0x05,0x0B,0x02,0x00,0x00,0x00,0x00,(byte)0x84,0x6A,(byte)0xE8,0x73,0x00,0x03,(byte)0x9B,(byte)0xEA};
        writeData(b,b.length);
        readData(bytes);



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
