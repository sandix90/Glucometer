package org.sandix.glucometer.asyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.os.AsyncTask;

import org.sandix.glucometer.beans.GlBean;
import org.sandix.glucometer.interfaces.AsyncTaskCompleteListener;
import org.sandix.glucometer.models.UsbGlucometerDevice;

/**
 * Created by Alex on 04.05.2016.
 */
public class AsyncGlucometerExecutor extends AsyncTask<Void,Void,Object> {
    public final static int VALUES_COUNT = 0;
    public final static int SERIAL_NUMBER = 1;
    public final static int VALUE = 2;

    private ProgressDialog dialog;
    private Context context;
    private int request_type;
    private int index;

    private UsbDevice mUsbDevice;
    private UsbDeviceConnection mUsbDeviceConnection;
    private UsbGlucometerDevice glucometerDevice;

    private AsyncTaskCompleteListener mCallback;


    public AsyncGlucometerExecutor(Context context, int type, UsbDevice usbDevice, UsbDeviceConnection usbDeviceConnection){

        this.context = context;
        this.request_type = type;
        this.mUsbDevice = usbDevice;
        this.mUsbDeviceConnection = usbDeviceConnection;
        dialog = new ProgressDialog(this.context);
        dialog.setTitle("Пожалуйста подождите");
        dialog.setMessage("Получение данных с глюкометра...");
        dialog.setCanceledOnTouchOutside(true);

    }
    public void setAsyncTaskCompleteListener(AsyncTaskCompleteListener listener){
        this.mCallback = listener;
    }


    /*
    * This method use to set index to get value from glucometer by index<br>
    */
    public void setIndexVar(int index){
        this.index = index;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

    @Override
    protected Object doInBackground(Void... params) {
        switch (request_type){
            case VALUES_COUNT:
                return getGlucometerRecordCount();
            case SERIAL_NUMBER:
                return getGlucometerSN();
            case VALUE:
                if(index!=-1) {
                    return getGlucometerRecord(index);
                }
                return null;

        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        mCallback.onTaskComplete(o,request_type);
        dialog.dismiss();
    }

    private String getGlucometerSN(){
        if(mUsbDevice!=null){
            glucometerDevice = UsbGlucometerDevice.initializeUsbDevice(mUsbDevice, mUsbDeviceConnection);
            if(glucometerDevice!=null) {
                glucometerDevice.open();
                String sn = glucometerDevice.getSN();
                glucometerDevice.close();
                return sn;
            }
        }
        return null;
    }

    private GlBean getGlucometerRecord(int index){
        if(mUsbDevice!=null){
            glucometerDevice = UsbGlucometerDevice.initializeUsbDevice(mUsbDevice,mUsbDeviceConnection);
            if(glucometerDevice!=null) {
                glucometerDevice.open();
                GlBean bean;
                bean = glucometerDevice.getRecord(index);
                //Log.d("MainAct", "Str length: " + str.length);
                //mMessage.setText("Data: " + str[0] + " Value: " + str[1]);
                glucometerDevice.close();
                return bean;
            }
        }
        return null;
    }

    private int getGlucometerRecordCount(){
        if(mUsbDevice!=null) {
            glucometerDevice = UsbGlucometerDevice.initializeUsbDevice(mUsbDevice, mUsbDeviceConnection);
            if (glucometerDevice != null) {
                glucometerDevice.open();

                int count = glucometerDevice.getRecordsCount();
                glucometerDevice.close();
                return count;
            }
        }
        return -1;
    }
}
