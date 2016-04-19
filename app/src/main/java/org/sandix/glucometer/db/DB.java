package org.sandix.glucometer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by sandakov.a on 18.04.2016.
 */
public class DB {
    private Context context;
    private  DBHelper mDbHelper;
    private SQLiteDatabase mDB;

    public DB(Context context){
        this.context = context;
    }

    public void open(){
        mDbHelper = new DBHelper(context);
        mDB = mDbHelper.getWritableDatabase();
    }

    public void close(){
        if(mDbHelper!=null)
            {mDB.close();}
    }
    public void addRecord(){
        ContentValues cv = new ContentValues();
        cv.put("serialnumber","CN25Jf874");
        cv.put("last_name","Сандаков");
        cv.put("first_name","Алексей");
        cv.put("middle_name","Сергеевич");
        cv.put("age",25);
        cv.put("therapy_type","Без терапии");
        cv.put("gender",true);
        cv.put("phone","8-922-632-15-57");
        cv.put("diabetes_type","Нет диабета");
        cv.put("email","sandakov.aleksey65@gmail.com");
        mDB.insert("main",null,cv);




    }
}
