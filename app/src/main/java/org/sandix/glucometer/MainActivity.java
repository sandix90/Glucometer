package org.sandix.glucometer;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.sandix.glucometer.adapters.MainListUsersAdapter;
import org.sandix.glucometer.asyncTasks.AsyncDbExecutor;
import org.sandix.glucometer.asyncTasks.AsyncGlucometerExecutor;
import org.sandix.glucometer.beans.GlBean;
import org.sandix.glucometer.beans.UserBean;
import org.sandix.glucometer.db.DBHelper;
import org.sandix.glucometer.interfaces.AsyncTaskCompleteListener;
import org.sandix.glucometer.models.UsbGlucometerDevice;
import org.sandix.glucometer.synchronizers.Synchronizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AsyncTaskCompleteListener {
    UsbManager mUsbManager;
    ListView deviceList;
    Button refreshBtn;
    TextView mMessage;
    UsbDevice mUsbDevice;
    UsbGlucometerDevice glucometerDevice;

    UsbDeviceConnection mUsbDeviceConnection;
    PendingIntent mPermissionIntent;
    RecyclerView main_list;
    FloatingActionButton fab;
    LinearLayoutManager llm;
    Toolbar toolbar;

    private static final String ACTION_USB_PERMISSION =
            "org.sandix.glucometer.USB_PERMISSION";


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsbManager = (UsbManager) getSystemService(USB_SERVICE);
//        refreshBtn = (Button) findViewById(R.id.refresh_btn);
//        refreshBtn.setOnClickListener(this);
        mMessage = (TextView) findViewById(R.id.message);
        main_list = (RecyclerView) findViewById(R.id.main_list);
        llm = new LinearLayoutManager(this);
        main_list.setLayoutManager(llm);
        fab = (FloatingActionButton) findViewById(R.id.add_user);
        fab.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Список больных");

        getUsersInfo();
        //ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
        //backupDataBase("backup");

        mUsbDevice = getIntent().getParcelableExtra(mUsbManager.EXTRA_DEVICE);
        if(mUsbDevice != null){
            //Toast.makeText(MainActivity.this, "mUsbDevice:" +mUsbDevice.toString() +", VID: "+mUsbDevice.getVendorId()+" PID:"+mUsbDevice.getProductId(), Toast.LENGTH_SHORT).show();
            mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
            synchronize();
            //TODO: Здесь будет вызываться функция автоматического получения инфы с глюкометра
        }

        mPermissionIntent = PendingIntent.getBroadcast(this,0,new Intent(ACTION_USB_PERMISSION),0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

    }



    private void getUsersInfo() {
        AsyncDbExecutor dbExecutor = new AsyncDbExecutor(this); //Без доп. аргументов, значит вытащить всех пользователей. см.AsyncDbExecutor
        dbExecutor.setOnTaskCompleteListener(this);
        dbExecutor.execute(); //В классе вызывается event onTaskComplete(obj, type) тип запроса.
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
//            case R.id.refresh_btn:
//                tmp();
//                break;
//            case R.id.get_info:
//                //communicate();
//                Intent intent = new Intent(this, DetailedInfoClientForm.class);
//                startActivity(intent);
//                break;
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
                break;
            case R.id.getfirstrecord:
                AsyncGlucometerExecutor record_executor = new AsyncGlucometerExecutor(this, AsyncGlucometerExecutor.VALUE, mUsbDevice, mUsbDeviceConnection);
                record_executor.setAsyncTaskCompleteListener(this);
                record_executor.execute();
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
                if(result!=null) {
                    mMessage.setText("SN: " + result.toString());
                }
                break;
            case AsyncGlucometerExecutor.VALUES_COUNT:
                if((int)result>0) {
                    mMessage.setText("Count: " + String.valueOf(result));
                }
                break;
            case AsyncGlucometerExecutor.VALUE:
                if(result!=null) {
                    GlBean glBean = (GlBean) result;
                    mMessage.setText("Value: "+glBean.getGl_value()+ " Date: "+glBean.getDate());
                }

                break;
            case AsyncDbExecutor.DB_TASK_SELECT_ALL:
                Cursor all_users_cursor = (Cursor)result;
                List<UserBean> userBeanList = new ArrayList<>();
                if(all_users_cursor!=null) {
                    if (all_users_cursor.moveToFirst()) {
                        do {
                            userBeanList.add(new UserBean(all_users_cursor.getInt(all_users_cursor.getColumnIndex("id")),
                                    all_users_cursor.getString(all_users_cursor.getColumnIndex("serialnumber")),
                                    all_users_cursor.getString(all_users_cursor.getColumnIndex("last_name")),
                                    all_users_cursor.getString(all_users_cursor.getColumnIndex("first_name")),
                                    all_users_cursor.getString(all_users_cursor.getColumnIndex("middle_name")),
                                    all_users_cursor.getInt(all_users_cursor.getColumnIndex("age")),
                                    all_users_cursor.getString(all_users_cursor.getColumnIndex("email")),
                                    all_users_cursor.getString(all_users_cursor.getColumnIndex("therapy_type")),
                                    all_users_cursor.getString(all_users_cursor.getColumnIndex("diabetic_type")),
                                    all_users_cursor.getString(all_users_cursor.getColumnIndex("phone")),
                                    all_users_cursor.getInt(all_users_cursor.getColumnIndex("gender")) == 1 ? "Мужской" : "Женский",
                                    all_users_cursor.getString(all_users_cursor.getColumnIndex("comments"))
                            ));
                        } while (all_users_cursor.moveToNext());

                        MainListUsersAdapter listUsersAdapter = new MainListUsersAdapter(this, userBeanList);
                        listUsersAdapter.setHasStableIds(true);
                        main_list.setAdapter(listUsersAdapter);
                    }
                }
                break;
            case AsyncDbExecutor.DB_TASK_WITH_CONDITION:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void synchronize() {
        final String[] sn = {""};
        AsyncGlucometerExecutor exec = new AsyncGlucometerExecutor(this, AsyncGlucometerExecutor.SERIAL_NUMBER,mUsbDevice,mUsbDeviceConnection);
        exec.setAsyncTaskCompleteListener(new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int request_type) {
                sn[0] = (String)result;
                Log.d("MainActivity", (String)result);
                //mMessage.setText((String)result);
                Synchronizer synchronizer = new Synchronizer(MainActivity.this,(String)result, mUsbDevice,mUsbDeviceConnection);
                synchronizer.execute();
            }
        });
        exec.execute();



    }

    public void backupDataBase(String _filename) {
        DBHelper dbHelper= new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        File dbFile = new File(db.getPath());
        File backupDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"Glucomaster");
        File backupFile = new File(backupDir,_filename+".backup");
        if(!backupDir.exists()){
            if(!backupDir.mkdirs()){
                return;
            }
        }
        try {
            FileChannel source = new FileInputStream(dbFile).getChannel();
            FileChannel destination = new FileOutputStream(backupFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            //Toast.makeText(context,"Erfolgreich!",Toast.LENGTH_SHORT).show();

        } catch(IOException e) {
            e.printStackTrace();
        }

    }
}


