package org.sandix.glucometer;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.sandix.glucometer.beans.Bean;
import org.sandix.glucometer.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandakov.a on 19.04.2016.
 */
public class DbAsyncTaskLoader extends AsyncTaskLoader<List<Bean>> {

    DBHelper dbHelper;
    SQLiteDatabase db;
    List<Bean> list;


    public DbAsyncTaskLoader(Context context){
        super(context);
    }
    @Override
    public List<Bean> loadInBackground() {
        list = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        Cursor c = db.query("main",null,null,null,null,null,null);
        if(c.moveToFirst()){
            do{

            }while(c.moveToNext());
        }



        return null;

    }

}
