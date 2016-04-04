package org.sandix.glucometer;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        }
//        BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                if(ACTION_USB_PERMISSION.equals(action)){
//                    synchronized (this){
//                        mUsbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//
//                        if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)){
//                            if(mUsbDevice != null){
//                                communicate();
//                            }
//                        }
//                        else{
//                            Toast.makeText(MainActivity.this, "Permission denied for device "+mUsbDevice, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//        };
        mPermissionIntent = PendingIntent.getBroadcast(this,0,new Intent(ACTION_USB_PERMISSION),0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

//        h = new android.os.Handler(){
//           public void handleMessage(android.os.Message msg){
//               mMessage.setText(msg.what);
//           }
//        };
//        registerReceiver(mUsbReceiver, filter);



//        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},133);
//        }
//        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
//        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        filter.addAction(UsbManager.EXTRA_PERMISSION_GRANTED);
//        filter.addAction(ACTION_USB_PERMISSION);
        //registerReceiver(MyReciever.class,filter);


//        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                if(!mUsbManager.hasPermission(usbDevice)){
//                //if(action.equals(ACTION_USB_PERMISSION)){
//
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(ACTION_USB_PERMISSION), 0);
//                    mUsbManager.requestPermission(usbDevice, pendingIntent);
//                }
//
//
//                if(mUsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)){
//                    mMessage.setText("Device ID: "+usbDevice.getVendorId() + " successfully disconnected");
//                    isConnected=false;
//                    return;
//                }
//
//                if(mUsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)){
//                    mMessage.setText("Device ID: "+usbDevice.getVendorId() + " successfully connected");
//                    isConnected=true;
//                    return;
//                }
//                //message.setText(intent.toString());
//            }
//
//        };
      //  registerReceiver(broadcastReceiver, filter);
       // IntentFilter intentFilter = new IntentFilter("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        //IntentFilter intentFilter1 = new IntentFilter("android.hardware.usb.action.USB_DEVICE_DETACHED");
        //registerReceiver(broadcastReceiver,intentFilter);
        //registerReceiver(broadcastReceiver,intentFilter1);

        //findDevice();
    }

    private void tmp(){
        try {
            List<String> items = new ArrayList<>();
            //Toast.makeText(MainActivity.this, mUsbManager.getDeviceList().values().toString(), Toast.LENGTH_SHORT).show();
            for (UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {

                items.add("DeviceName: "+ usbDevice.getDeviceName());
                items.add("DeviceID: "+ String.valueOf(usbDevice.getDeviceId()));
                items.add("DeviceClass: "+ String.valueOf(usbDevice.getDeviceClass()));
                items.add("DeviceProtocol: "+ String.valueOf(usbDevice.getDeviceProtocol()));
                items.add("DeviceVendorID: "+ String.valueOf(usbDevice.getVendorId()));
                items.add("DeviceProductID: "+ usbDevice.getProductId());
               // items.add("DeviceManufName: "+ usbDevice.getManufacturerName());


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
//        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
//        Iterator<UsbDevice> iterator = deviceList.values().iterator();
//        while(iterator.hasNext()){
//            usbDevice = iterator.next();

//        }
        if(!mUsbManager.hasPermission(mUsbDevice)){
            mUsbManager.requestPermission(mUsbDevice, mPermissionIntent);
        }
        //mUsbInterface = mUsbDevice.getInterface(0);
        mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);

        //UsbSerialDevice serialDevice = UsbSerialDevice.createUsbSerialDevice(mUsbDevice, mUsbDeviceConnection);
        //serialDevice.open();
        UsbGlucometerDevice dev = UsbGlucometerDevice.initializeUsbDevice(mUsbDevice,mUsbDeviceConnection);
        if(dev!=null) {
            dev.open();
            mMessage.setText(dev.getSN());
            dev.close();
        }
//        byte[] startCmd = {0x02,0x06,0x08,0x03,(byte)0xC2,0x62};
//        byte[] getSN = {0x02, 0x12,0x00, 0x05,0x0B,0x02,0x00,0x00,0x00,0x00,(byte)0x84,0x6A, (byte)0xE8,0x73,0x00,0x03,(byte)0x9B,(byte)0xEA};
//
//        byte[] readRecCount = {0x02,0x0A,0x00,0x05,0x1F, (byte) 0xF5,0x01,0x03,0x038, (byte) 0xAA};
//        serialDevice.syncOpen();
//        serialDevice.syncWrite(getSN, 0);
//        byte[] buf = new byte[serialDevice.getInEndPointBufferSize()];
//        try {
//            Thread.sleep(2000);
//        }
//        catch (Exception ex){
//            ex.printStackTrace();
//        }
//        int n = serialDevice.syncRead(buf, 10);
//        String str = new String(Arrays.copyOfRange(buf,11,20), StandardCharsets.UTF_8);
//        mMessage.setText(serialDevice.getRecordsCount());
//        serialDevice.syncClose();



//        mMessage.setText("l:"+buf.length+" d:"+buf[0]+" "+buf[1]+" "+buf[2]+
//                " "+buf[3]+" "+buf[4]+
//                " "+buf[5]+" "+buf[6]+" "+buf[7]+" "+buf[8]+" "+buf[9]+" "+buf[10]+" "+buf[11]+" "+buf[12]+" "+buf[13]+" "+buf[14]+" "+buf[15]+" "+buf[16]+" "+buf[17]+buf[18]+"\n hexTostr: "+ new String(buf, StandardCharsets.UTF_8));
        //serialDevice.read(mCallback);
        //erialDevice.close();


//        mUsbDeviceConnection.claimInterface(mUsbInterface, true);
//        //mMessage.setText("Endpoint IN Direction is: " + mUsbEndPointIn.getDirection() + ", EndPoint OUT Direction is:" + mUsbEndPointOut.getDirection());
//       // mUsbDeviceConnection.claimInterface(mUsbInterface, true);
//        //byte[] getSerialNum = {0x02};
//        byte[] getSerialNum = {0x02,0x09,0x00,0x05,0x0D,0x02,0x03,(byte)0xDA,0x71};
//        byte[] getsn = {0x02, 0x12,0x00, 0x05,0x0B,0x02,0x00,0x00,0x00,0x00,(byte)0x84,0x6A, (byte)0xE8,0x73,0x00,0x03,(byte)0x9B,(byte)0xEA};
//
//        int num = mUsbDeviceConnection.bulkTransfer(mUsbEndPointOut, startcmd, startcmd.length, 10);
//        if(num>0){
//            mMessage.setText("num:" +num);
//        }
//
//        //String serial = mUsbDeviceConnection.getSerial();
//
//        new android.os.Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                byte[] buf = new byte[9];
//                int recv_num = mUsbDeviceConnection.bulkTransfer(mUsbEndPointIn, buf, buf.length, 0);
//
//                mMessage.setText("ans1:" + + buf[0] + buf[1] + buf[2] + buf[3]+ buf[4]+ buf[5]+ buf[6]+ buf[7]+ buf[8]);
//            }
//        });
//
////        new android.os.Handler().post(new Runnable() {
////            @Override
////            public void run() {
////                byte[] buf2 = new byte[mUsbEndPointIn.getMaxPacketSize()];
////                //int recv_num = mUsbDeviceConnection.bulkTransfer(mUsbEndPointIn, buf, buf.length, 0);
////
////                mMessage.setText("ans2:" + buf2[0] + buf2[1] + buf2[2] + buf2[3]+ buf2[4]+ buf2[5]+ buf2[6]+ buf2[7]+ buf2[8]);
////            }
////        });
//
//        mUsbDeviceConnection.close();
//        MyRunnable runn = new  MyRunnable(this, mMessage, mUsbDeviceConnection, mUsbEndPointIn);
//        Thread thread = new Thread(runn);
//        thread.start();

//        UsbTreadTx thread = new UsbTreadTx(mUsbDeviceConnection,mUsbEndPointIn,this);
//        thread.ru



//        byte[] buf = new byte[mUsbEndPointIn.getMaxPacketSize()];
//        int recv_num = mUsbDeviceConnection.bulkTransfer(mUsbEndPointIn, buf, buf.length,0);
//        mMessage.setText("recv_num: "+recv_num+" answ:"+buf);
//        Toast.makeText(MainActivity.this, "answ: "+buf.toString(), Toast.LENGTH_SHORT).show();
        //mUsbDeviceConnection.controlTransfer(UsbConstants.USB_DIR_OUT)
//        if(recv_num>0){
//
//        }



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
}
