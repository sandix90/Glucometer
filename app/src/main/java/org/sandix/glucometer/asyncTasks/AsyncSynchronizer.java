package org.sandix.glucometer.asyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import org.sandix.glucometer.beans.GlBean;
import org.sandix.glucometer.db.DB;
import org.sandix.glucometer.models.UsbGlucometerDevice;

import java.util.List;

/**
 * Increment synchronize data from glucometer to db<br>
 * Only add/update DB data<br>
 * No operation for glucometer device<br>
 */
public class AsyncSynchronizer extends AsyncTask<Void,Integer,Void> {

    Context context;
    DB db;
    List<GlBean> glBeanList;
    ProgressDialog dialog;
    UsbGlucometerDevice glucometerDevice;

    public AsyncSynchronizer(Context context, List<GlBean> list, UsbGlucometerDevice device){
        this.glBeanList = list;
        this.context = context;
        this.glucometerDevice = device;
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
        //Сраниваем значения глюкометра и значения в БД.
        db = new DB(context);
        db.open();
        for (GlBean bean: glBeanList) {
            publishProgress(glBeanList.indexOf(bean));
            Cursor c = db.execute("SELECT * FROM values_table WHERE serial_num='"+glucometerDevice.getSN()+"' AND gluc_value='"+bean.getGl_value()+"' AND value_date='"+bean.getDate()+"';");
            if(c.moveToFirst()){//Нашли значение в БД, добавлять не нужно
                continue;
            }
            else{//Значения нет, добавляем
                db.addValueToGlValuesTable(glucometerDevice.getSN(),bean);
            }

        }
        db.close();
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dialog.setMessage("Обработка значения "+values[0]+" из "+glBeanList.size());

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
    }

}
