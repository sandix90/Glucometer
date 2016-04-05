package org.sandix.glucometer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by sandakov.a on 05.04.2016.
 */
public class MyAsyncTask extends AsyncTask<Integer,String,Void> {
    Context context;

    public MyAsyncTask(Context context){
        this.context = context;

    }

    ProgressDialog progressDialog;


    @Override
    protected Void doInBackground(Integer... params) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context,
                "ProgressDialog",
                "Получение данных");
        progressDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }
}
