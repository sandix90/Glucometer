package org.sandix.glucometer;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import android.os.AsyncTask;
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
import java.util.Observable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    UsbManager mUsbManager;
    ListView deviceList;
    Button refreshBtn, getInfoBtn;
    TextView mMessage;
    UsbDevice mUsbDevice;
    UsbGlucometerDevice glucometerDevice;

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
                //communicate();
                Intent intent = new Intent(this, ClientForm.class);
                startActivity(intent);
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
                //MyAsyncTask t = new MyAsyncTask(MainActivity.this);
                getGlucometerSN();
                break;
            case R.id.getfirstrecord:
                getGlucometerRecord();
                break;
            case R.id.getrecordscount:
                AsyncGlucometerExecutor task = new AsyncGlucometerExecutor(this);
                task.execute();
                //getGlucometerRecordCount();
                break;
        }
        return true;
    }

    private void getGlucometerSN(){
        if(mUsbDevice!=null){
            glucometerDevice = UsbGlucometerDevice.initializeUsbDevice(mUsbDevice, mUsbDeviceConnection);
            if(glucometerDevice!=null) {
                glucometerDevice.open();
                mMessage.setText("SN: " + glucometerDevice.getSN());
                glucometerDevice.close();
            }
        }
    }

    private void getGlucometerRecord(){
        if(mUsbDevice!=null){
            glucometerDevice = UsbGlucometerDevice.initializeUsbDevice(mUsbDevice,mUsbDeviceConnection);
            if(glucometerDevice!=null) {
                glucometerDevice.open();
                String[] str;
                str = glucometerDevice.getRecord(0);
                Log.d("MainAct", "Str length: " + str.length);
                mMessage.setText("Data: " + str[0] + " Value: " + str[1]);
                glucometerDevice.close();
            }
        }
    }

    private int getGlucometerRecordCount(){
        if(mUsbDevice!=null) {
            glucometerDevice = UsbGlucometerDevice.initializeUsbDevice(mUsbDevice, mUsbDeviceConnection);
            if (glucometerDevice != null) {
                glucometerDevice.open();
                int count = glucometerDevice.getRecordsCount();
                // mMessage.setText("Count: " + String.valueOf(glucometerDevice.getRecordsCount()));
                glucometerDevice.close();
                return count;
            }
        }
        return -1;
    }

    class AsyncGlucometerExecutor extends AsyncTask<Void,Integer,Void>{
        private ProgressDialog dialog;
        private boolean running;
        private Context context;


        public AsyncGlucometerExecutor(Context context){
            this.context = context;
            dialog = new ProgressDialog(context);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setTitle("Выполнение ассинхронной операции");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            running = true;
            dialog.setMessage("Получение данных с глюкометра");
            dialog.setCanceledOnTouchOutside(true);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    running = false;
                }
            });
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            int i = 10;
            while(running){
                if(i>=0){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(i);
                    i--;
                }
                else{
                    running=false;
                }
            }
            //return getGlucometerRecordCount();
            return null;
        }





        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setMessage("Значение "+String.valueOf(values[0]));
        }

        @Override
        protected void onPostExecute(Void value) {
//            if(value>0) {
//                mMessage.setText("Получено " + value + " записей");
//            }
//            else{
//                mMessage.setText("Ошибка чтения количества записей с устройства");
//            }
            Toast.makeText(context,"Операция завершена", Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }


    }


}


