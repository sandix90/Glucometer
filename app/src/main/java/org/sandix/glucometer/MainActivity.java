package org.sandix.glucometer;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.sandix.glucometer.adapters.MainListUsersAdapter;
import org.sandix.glucometer.asyncTasks.AsyncGlucometerExecutor;
import org.sandix.glucometer.beans.MainListBean;
import org.sandix.glucometer.db.DBHelper;
import org.sandix.glucometer.interfaces.AsyncTaskCompleteListener;
import org.sandix.glucometer.models.UsbGlucometerDevice;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AsyncTaskCompleteListener {
    UsbManager mUsbManager;
    ListView deviceList;
    Button refreshBtn, getInfoBtn;
    TextView mMessage;
    UsbDevice mUsbDevice;
    UsbGlucometerDevice glucometerDevice;

    UsbDeviceConnection mUsbDeviceConnection;
    PendingIntent mPermissionIntent;
    RecyclerView main_list;
    FloatingActionButton fab;
//    android.os.Handler h;

    private static final String ACTION_USB_PERMISSION =
            "org.sandix.glucometer.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsbManager = (UsbManager) getSystemService(USB_SERVICE);
        //deviceList = (ListView) findViewById(R.id.device_list);
        refreshBtn = (Button) findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(this);
        mMessage = (TextView) findViewById(R.id.message);
        getInfoBtn = (Button) findViewById(R.id.get_info);
        getInfoBtn.setOnClickListener(this);
        main_list = (RecyclerView) findViewById(R.id.main_list);
        main_list.setLayoutManager(new LinearLayoutManager(this));
        fab = (FloatingActionButton) findViewById(R.id.add_user);

        fab.setOnClickListener(this);


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
                Intent intent = new Intent(this, DetailedInfoClientForm.class);
                startActivity(intent);
                break;
            case R.id.add_user:
                Intent i = new Intent(this, EditClientForm.class);
                startActivity(i);
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
                AsyncGlucometerExecutor executor = new AsyncGlucometerExecutor(this, AsyncGlucometerExecutor.SERIAL_NUMBER, mUsbDevice,mUsbDeviceConnection);
                executor.setAsyncTaskCompleteListener(this);
                executor.execute();
//                try {
//                    String sn = (String)executor.get();
//                    mMessage.setText("SN: "+sn);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
                //MyAsyncTask t = new MyAsyncTask(MainActivity.this);
                //getGlucometerSN();
                break;
            case R.id.getfirstrecord:
                //getGlucometerRecord();
                break;
            case R.id.getrecordscount:
                AsyncGlucometerExecutor executor1 = new AsyncGlucometerExecutor(this, AsyncGlucometerExecutor.VALUES_COUNT, mUsbDevice, mUsbDeviceConnection);
                executor1.setAsyncTaskCompleteListener(this);
                executor1.execute();
//                try {
//                    int count = (int)executor1.get();
//                    mMessage.setText("count: "+count);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }


//                AsyncGlucometerExecutor1 task = new AsyncGlucometerExecutor1(this);
//                task.execute();
//                try {
//                    Cursor c = task.get();
//
//                    List<MainListBean> mData = new ArrayList<>();
//                    if(c.moveToFirst()){
//                        do{
//                            mData.add(new MainListBean(c.getInt(c.getColumnIndex("id")),c.getString(c.getColumnIndex("last_name")),c.getString(c.getColumnIndex("first_name"))));
//                        }while(c.moveToNext());
//                        MainListUsersAdapter adapter = new MainListUsersAdapter(this, mData);
//                        main_list.setAdapter(adapter);
//
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }

                //getGlucometerRecordCount();
                break;
        }
        return true;
    }

    @Override
    public void onTaskComplete(Object result, int request_type) {
        switch (request_type){
            case AsyncGlucometerExecutor.SERIAL_NUMBER:
                mMessage.setText("SN: "+result.toString());
                break;
            case AsyncGlucometerExecutor.VALUES_COUNT:
                mMessage.setText("Count: "+String.valueOf(result));
                break;
            case AsyncGlucometerExecutor.VALUE:
                //mMessage.setText("Value: "+(String[])result[0]+" "+(String[])result[1]);
                break;
        }
    }


    class AsyncGlucometerExecutor1 extends AsyncTask<Void,Integer,Cursor>{
        private ProgressDialog dialog;
        private boolean running;
        private Context context;
        private Cursor cursor;
        private DBHelper dbHelper;
        private SQLiteDatabase db;


        public AsyncGlucometerExecutor1(Context context){
            this.context = context;
            dialog = new ProgressDialog(context);
            dialog.setCanceledOnTouchOutside(true);
            //dialog.setTitle("Выполнение ассинхронной операции");
            dbHelper = new DBHelper(context);
            db = dbHelper.getReadableDatabase();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            running = true;
            //dialog.setMessage("Получение данных из БД");
            dialog.setCanceledOnTouchOutside(true);
//            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    running = false;
//                }
//            });
            dialog.show();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
//            int i = 10;
//            while(running){
//                if(i>=0){
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    publishProgress(i);
//                    i--;
//                }
//                else{
//                    running=false;
//                }
//            }
            //return getGlucometerRecordCount();
            cursor = db.query("main",new String[]{"id","last_name","first_name","serial_number"},null,null,null,null,null);

            return cursor;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //super.onProgressUpdate(values);
            dialog.setMessage("Значение "+String.valueOf(values[0]));
        }

        @Override
        protected void onPostExecute(Cursor value) {
            super.onPostExecute(value);
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


