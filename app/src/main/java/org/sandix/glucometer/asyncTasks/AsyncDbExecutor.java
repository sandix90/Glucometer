package org.sandix.glucometer.asyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import org.sandix.glucometer.db.DB;
import org.sandix.glucometer.interfaces.AsyncTaskCompleteListener;

/**
 * Created by sandakov.a on 05.05.2016.
 */
public class AsyncDbExecutor extends AsyncTask<Void,Void,Cursor>{
    public final static int DB_TASK_SELECT_ALL = 3;
    public final static int DB_TASK_WITH_CONDITION = 4;

    private Context context;
    private AsyncTaskCompleteListener mCallback;
    private Cursor result;
    private DB db;
    private int type;
    private String query ="";
    private ProgressDialog dialog;

    public AsyncDbExecutor(Context context){
        this.context =context;
        type = DB_TASK_SELECT_ALL;
    }

    public AsyncDbExecutor(Context context, String query){
        this.context = context;
        type = DB_TASK_WITH_CONDITION;
        this.query = query;
    }

    public void setOnTaskCompleteListener(AsyncTaskCompleteListener listener){
        this.mCallback = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle("Пожалуйста подождите");
        dialog.setMessage("Выполняется запрос к базе данных...");
        dialog.show();
    }

    @Override
    protected Cursor doInBackground(Void... params) {
        db = new DB(context);
        db.open();
        if(query.equals("")){
            result = db.getAllUsers();
        }
        else {
            result = db.stringQuery(query);
        }
        //db.close();
        if(result.getCount()==0){
            return  null;
        }

        return result;
    }

    @Override
    protected void onPostExecute(Cursor result) {
        super.onPostExecute(result);
        mCallback.onTaskComplete(result,type);
        db.close();
        dialog.dismiss();
    }
}
