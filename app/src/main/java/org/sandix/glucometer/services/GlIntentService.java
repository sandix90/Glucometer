package org.sandix.glucometer.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.sandix.glucometer.beans.GlBean;
import org.sandix.glucometer.db.DB;
import org.sandix.glucometer.models.GlucometerProxy;
import org.sandix.glucometer.models.UsbGlucometerDevice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GlIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */


    public final static String ACTION_GLINTENTSERVICE = "org.sandix.glucometer.intentservice.RESPONSE";
    public final static int VALUES_COUNT = 0;
    public final static int SERIAL_NUMBER = 1;
    public final static int VALUES = 2;
    public final static int SYNC = 3;

    private int index_from = 0;
    private int index_to = 0;
    private int mRecordsCount =0 ;

    private List<GlBean> glBeanList;


    public GlIntentService() {
        super("glIntentService");

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
            if(intent.hasExtra("type")){
                if(intent.getIntExtra("type", -1)==SERIAL_NUMBER){ //Получаем серийник
                    Intent snIntent = new Intent();
                    snIntent.setAction(ACTION_GLINTENTSERVICE);
                    snIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    snIntent.putExtra("serial_number", GlucometerProxy.getInstance().getSerialNumber());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(snIntent);
                }
                else if (intent.getIntExtra("type", -1)==SYNC){ //Синхронизируем
                    if(!GlucometerProxy.getInstance().isGlucometerNull()){
                        mRecordsCount = GlucometerProxy.getInstance().getValuesCount();
                        glBeanList = new ArrayList<>();
                        for(int i =0;i<mRecordsCount;i++){
                            glBeanList.add(GlucometerProxy.getInstance().getValueByIndex(i));
                        }

                        //DB sync
                        DB db = new DB(this);
                        db.open();
                        for (GlBean bean: glBeanList) {
                            Cursor c = db.execute("SELECT * FROM values_table WHERE serial_num='"+GlucometerProxy.getInstance().getSerialNumber()+
                                    "' AND gluc_value='"+bean.getGl_value()+"' AND value_date='"+bean.getDate()+"';");
                            if(c.moveToFirst()){//Нашли значение в БД, добавлять не нужно
                                continue;
                            }
                            else{//Значения нет, добавляем
                                db.addValueToGlValuesTable(GlucometerProxy.getInstance().getSerialNumber(),bean);
                            }
                        }
                        db.close();

                        Intent syncIntentResponse = new Intent();
                        syncIntentResponse.setAction(ACTION_GLINTENTSERVICE);
                        syncIntentResponse.putExtra("sync_response",true);
                        sendBroadcast(syncIntentResponse);


//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("gl_bean_list", (Serializable) glBeanList);
//
//                        Intent valuesResponseIntent = new Intent();
//                        valuesResponseIntent.setAction(ACTION_GLINTENTSERVICE);
//                        valuesResponseIntent.addCategory(Intent.CATEGORY_DEFAULT);
//                        valuesResponseIntent.putExtras(bundle);
//                        sendBroadcast(valuesResponseIntent);

                    }

                }

            }
    }




    public void setIndexes( int from , int to ){
        this.index_from = from;
        this.index_to = to;
    }

//    private String getGlucometerSN(){
//            if(mGlucometerDevice!=null) {
//                mGlucometerDevice.open();
//                String sn = mGlucometerDevice.getSN();
//                mGlucometerDevice.close();
//                return sn;
//            }
//        return null;
//    }
//
//    private GlBean getGlucometerRecord(int index){
//            if(mGlucometerDevice!=null) {
//                mGlucometerDevice.open();
//                GlBean bean;
//                bean = mGlucometerDevice.getRecord(index);
//                //Log.d("MainAct", "Str length: " + str.length);
//                //mMessage.setText("Data: " + str[0] + " Value: " + str[1]);
//                mGlucometerDevice.close();
//                return bean;
//            }
//        return null;
//    }
//
//    private int getGlucometerRecordCount(){
//            if (mGlucometerDevice != null) {
//                mGlucometerDevice.open();
//                int count = mGlucometerDevice.getRecordsCount();
//                mGlucometerDevice.close();
//                return count;
//            }
//        return -1;
//    }

}
