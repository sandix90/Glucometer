package org.sandix.glucometer;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.sandix.glucometer.models.UsbGlucometerDevice;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    UsbManager mUsbManager;
    ListView deviceList;
    Button refreshBtn, getInfoBtn;
    TextView mMessage;
    UsbDevice mUsbDevice;

    UsbDeviceConnection mUsbDeviceConnection;
    PendingIntent mPermissionIntent;
//    android.os.Handler h;

    private static final String ACTION_USB_PERMISSION =
            "org.sandix.glucometer.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsbManager = (UsbManager) getSystemService(USB_SERVICE);
        deviceList = (ListView) findViewById(R.id.device_list);
        refreshBtn = (Button) findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(this);
        mMessage = (TextView) findViewById(R.id.message);
        getInfoBtn = (Button) findViewById(R.id.get_info);
        getInfoBtn.setOnClickListener(this);


        mUsbDevice = getIntent().getParcelableExtra(mUsbManager.EXTRA_DEVICE);
        if(mUsbDevice != null){
            Toast.makeText(MainActivity.this, "mUsbDevice:" +mUsbDevice.toString() +", VID: "+mUsbDevice.getVendorId()+" PID:"+mUsbDevice.getProductId(), Toast.LENGTH_SHORT).show();
            mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
        }

        mPermissionIntent = PendingIntent.getBroadcast(this,0,new Intent(ACTION_USB_PERMISSION),0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
    }

    private void tmp(){
        try {
            List<String> items = new ArrayList<>();
            for (UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {

                items.add("DeviceName: "+ usbDevice.getDeviceName());
                items.add("DeviceID: "+ String.valueOf(usbDevice.getDeviceId()));
                items.add("DeviceClass: "+ String.valueOf(usbDevice.getDeviceClass()));
                items.add("DeviceProtocol: "+ String.valueOf(usbDevice.getDeviceProtocol()));
                items.add("DeviceVendorID: "+ String.valueOf(usbDevice.getVendorId()));
                items.add("DeviceProductID: "+ usbDevice.getProductId());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_expandable_list_item_1,items);
            deviceList.setAdapter(adapter);

        }catch (Exception e){
            //Toast.makeText(MainActivity.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
            mMessage.setText(e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.refresh_btn:
                tmp();
                break;
            case R.id.get_info:
                communicate();
                break;
        }

    }

    private void communicate() {

        if(!mUsbManager.hasPermission(mUsbDevice)){
            mUsbManager.requestPermission(mUsbDevice, mPermissionIntent);
        }
        //mUsbInterface = mUsbDevice.getInterface(0);


        //UsbSerialDevice serialDevice = UsbSerialDevice.createUsbSerialDevice(mUsbDevice, mUsbDeviceConnection);
        //serialDevice.open();
        UsbGlucometerDevice dev = UsbGlucometerDevice.initializeUsbDevice(mUsbDevice,mUsbDeviceConnection);
        if(dev!=null) {
            dev.open();
            mMessage.setText(dev.getSN());
            dev.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.getsn:
                MyAsyncTask t = new MyAsyncTask(MainActivity.this);
                UsbGlucometerDevice glucometerDevice = UsbGlucometerDevice.initializeUsbDevice(mUsbDevice,mUsbDeviceConnection);
                glucometerDevice.open();
                mMessage.setText("SN: " + glucometerDevice.getSN());
                glucometerDevice.close();
                break;
            case R.id.getfirstrecord:
                UsbGlucometerDevice glucometerDevice1 = UsbGlucometerDevice.initializeUsbDevice(mUsbDevice,mUsbDeviceConnection);
                glucometerDevice1.open();
                String[] str = new String[2];
                str = glucometerDevice1.getRecord(0);
                Log.d("MainAct","Str length: "+str.length);
                mMessage.setText("Data: "+ str[0]+" Value: "+str[1]);
                glucometerDevice1.close();
                break;
            case R.id.getrecordscount:
                UsbGlucometerDevice glucometerDevice2 = UsbGlucometerDevice.initializeUsbDevice(mUsbDevice,mUsbDeviceConnection);
                glucometerDevice2.open();
                mMessage.setText("Count: "+String.valueOf(glucometerDevice2.getRecordsCount()));
                glucometerDevice2.close();
                break;
        }
        return true;
    }
}


