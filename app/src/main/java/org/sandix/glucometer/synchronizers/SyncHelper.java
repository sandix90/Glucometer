package org.sandix.glucometer.synchronizers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.sandix.glucometer.asyncTasks.AsyncGlucometerExecutor;
import org.sandix.glucometer.asyncTasks.AsyncSynchronizer;
import org.sandix.glucometer.beans.GlBean;
import org.sandix.glucometer.db.DB;
import org.sandix.glucometer.interfaces.AsyncTaskCompleteListener;
import org.sandix.glucometer.models.UsbGlucometerDevice;
import org.sandix.glucometer.services.GlIntentService;

import java.util.ArrayList;
import java.util.List;

/*
* This class prepare all data for background sync
* */
public class SyncHelper {
    Context context;
    UsbGlucometerDevice glucometerDevice;
    DB db;
    int recordsCount;
    List<GlBean> glBeanList;
    GlBroadcastReceiver mGlBroadcastReceiver;
    IntentFilter mIntentFilter;

    public SyncHelper(Context context, UsbGlucometerDevice device){
        this.glucometerDevice = device;
        this.context = context;
        glBeanList = new ArrayList<>();

        mGlBroadcastReceiver = new GlBroadcastReceiver();
        mIntentFilter = new IntentFilter(GlIntentService.ACTION_GLINTENTSERVICE);
        mIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        context.registerReceiver(mGlBroadcastReceiver, mIntentFilter);

    }

//
    public void synchronize(){

        Intent glIntentServiceVC = new Intent(context, GlIntentService.class);
        glIntentServiceVC.putExtra("type", GlIntentService.VALUES_COUNT);

        GlIntentService glIntentService = new GlIntentService("glIntentService", glucometerDevice);
        glIntentService.startService(glIntentServiceVC);


        Intent glIntentServiceV = new Intent(context, GlIntentService.class);
        glIntentServiceV.putExtra("type", GlIntentService.VALUES);
        if(recordsCount>0){
            glIntentServiceV.putExtra("index_to", recordsCount);
        }

        //GlIntentService glIntentService1 = new GlIntentService("glIntentService", glucometerDevice);
        glIntentService.startService(glIntentServiceV);



        Intent DBSyncIntent = new Intent(context,GlIntentService.class);



//
//        //Получаем кол-во значений в глюкометре
//        AsyncGlucometerExecutor countExecutor= new AsyncGlucometerExecutor(context, AsyncGlucometerExecutor.VALUES_COUNT,
//                glucometerDevice.getUsbDevice(),glucometerDevice.getUsbDeviceConnection());
//        countExecutor.setAsyncTaskCompleteListener(new AsyncTaskCompleteListener() {
//            @Override
//            public void onTaskComplete(Object result, int request_type) {
//                recordsCount=(int)result;
//                getAllGlValues();
//                Log.d("Synchronizer", "On Task Complete: records count:"+recordsCount);
//            }
//        });
//        countExecutor.execute();


    }

    private void getAllGlValues(){
        //Достаем все значения из глюкометра
        //TODO Возможно прийдется сделать возможность задавать диапазон значений, которые нужно выстаскивать
        AsyncGlucometerExecutor valuesExecutor = null;
        for(int i=0;i<recordsCount;i++) {
            valuesExecutor = new AsyncGlucometerExecutor(context, AsyncGlucometerExecutor.VALUE,
                    glucometerDevice.getUsbDevice(),glucometerDevice.getUsbDeviceConnection());
            valuesExecutor.setIndexVar(i); //Устанаваливаем переменную индекса, чтоб по нему выстаскивать значения глюкометра
            valuesExecutor.setAsyncTaskCompleteListener(new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(Object result, int request_type) {
                    glBeanList.add((GlBean)result);
                }
            });
            valuesExecutor.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
        Log.d("Synchronizer", "glBeanList Size"+glBeanList.size() );


        AsyncSynchronizer synchronizer = new AsyncSynchronizer(context, glBeanList, glucometerDevice);
        synchronizer.execute();
       // Log.d("CountExecutor","isCanselled: "+countExecutor.isCancelled());


    }

    public class GlBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("values_count")){
                recordsCount = Integer.parseInt(intent.getStringExtra("values_count"));
            }
            if(intent.hasExtra("gl_bean_list")){
                glBeanList = (List<GlBean>) intent.getSerializableExtra("gl_bean_list");
            }

        }
    }


}
