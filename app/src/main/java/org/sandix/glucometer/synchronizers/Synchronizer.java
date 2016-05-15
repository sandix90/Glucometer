package org.sandix.glucometer.synchronizers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.os.AsyncTask;
import android.util.Log;

import org.sandix.glucometer.EditClientForm;
import org.sandix.glucometer.asyncTasks.AsyncDbExecutor;
import org.sandix.glucometer.asyncTasks.AsyncGlucometerExecutor;
import org.sandix.glucometer.beans.GlBean;
import org.sandix.glucometer.beans.UserBean;
import org.sandix.glucometer.db.DB;
import org.sandix.glucometer.interfaces.AsyncTaskCompleteListener;

import java.util.List;

/**
 * Increment synchronize data from glucometer to db<br>
 * Only add/update DB data<br>
 * No operation for glucometer device<br>
 */
public class Synchronizer extends AsyncTask<Void,Void,Void> implements Dialog.OnClickListener {

    Context context;
    DB db;
    AsyncDbExecutor dbExecutor;
    List<GlBean> glBeanList;
    String serial_number;
    UsbDevice mUsbDevice;
    UsbDeviceConnection mUsbDeviceConnection;
    ProgressDialog dialog;
    int recordsCount;

    public Synchronizer(Context context, String serial_number, UsbDevice usbDevice, UsbDeviceConnection usbDeviceConnection){
        this.context = context;
        this.serial_number = serial_number;
        this.mUsbDevice = usbDevice;
        this.mUsbDeviceConnection = usbDeviceConnection;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setTitle("Сихронизация");
        dialog.setMessage("Выполнение синхронизации данных с глюкометром...");
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        synchronize();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
    }

    public void setGlBeanList(List<GlBean> list){
        this.glBeanList = list;
    }

    public void synchronize(){
        if(glBeanList==null || serial_number.equals("")){
            return;
        }

        if(mUsbDevice==null || mUsbDeviceConnection==null){
            return;
        }

        db = new DB(context);
        db.open();


        UserBean userBean = db.checkIfExist(serial_number);
        if(userBean==null){ //Пользователя не существует. Предлагаем создать
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setMessage("Пациента с текущим глюкметром не существует в базе. Создать информацию о пациенте?").
//                    setPositiveButton("ОК",this).setNegativeButton("Отмена", this).show();
            Intent intent = new Intent(context, EditClientForm.class);
            context.startActivity(intent);
            return;
        }
        Log.d("Synchronizer", "Check user exist");
        //Продолжаем если пользователь существует

        //Получаем кол-во значений в глюкометре
        AsyncGlucometerExecutor countExecutor= new AsyncGlucometerExecutor(context, AsyncGlucometerExecutor.VALUES_COUNT,mUsbDevice,mUsbDeviceConnection);
        countExecutor.setAsyncTaskCompleteListener(new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int request_type) {
                recordsCount=(int)result;
            }
        });
        Log.d("Synchronizer", "Got values count");

        //Достаем все значения из глюкометра
        //TODO Возможно прийдется сделать возможность задавать диапазон значений, которые нужно выстаскивать
        for(int i=0;i<recordsCount;i++) {
            AsyncGlucometerExecutor valuesExecutor = new AsyncGlucometerExecutor(context, AsyncGlucometerExecutor.VALUE, mUsbDevice, mUsbDeviceConnection);
            valuesExecutor.setIndexVar(i);
            valuesExecutor.setAsyncTaskCompleteListener(new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(Object result, int request_type) {
                    glBeanList.add((GlBean)result);
                }
            });
        }

        //Сраниваем значения глюкометра и значения в БД.
        db = new DB(context);
        db.open();
        for (GlBean bean: glBeanList) {
            Cursor c = db.execute("SELECT * FROM values_table WHERE serial_num='"+serial_number+"' AND gluc_value='"+bean.getGl_value()+"' AND value_date='"+bean.getDate()+"';");
            if(c.moveToFirst()){//Нашли значение в БД, добавлять не нужно
                continue;
            }
            else{//Значения нет, добавляем
                db.addValueToGlValuesTable(serial_number,bean);
            }

        }
        db.close();

    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case Dialog.BUTTON_POSITIVE:
                Intent intent = new Intent(context, EditClientForm.class);
                //TODO Нужно добавить здесь заполение поля серийный номер в активи EditClientForm автоматически
                context.startActivity(intent);


                break;
        }

    }


}
