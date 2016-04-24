package org.sandix.glucometer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.sandix.glucometer.beans.UserBean;

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
    public void addRecordToMainTable(String serial_number, String last_name, String first_name, String middle_name, int age, String therapy_type, String diabetic_type, String gender, String phone_num, String email, String comments){
        ContentValues cv = new ContentValues();
        cv.put("serialnumber",serial_number);
        cv.put("last_name",last_name);
        cv.put("first_name",first_name);
        cv.put("middle_name",middle_name);
        cv.put("age",age);
        cv.put("therapy_type",therapy_type);
        if(gender.equals("Мужской")){
            cv.put("gender",true);
        }
        else{
            cv.put("gender",false);
        }
        cv.put("phone",phone_num);
        cv.put("diabetes_type",diabetic_type);
        cv.put("email",email);
        cv.put("comments", comments);

        mDB.insert("main",null,cv);

    }

    public UserBean getUserDataById(int user_id){

        Cursor c = mDB.query("main",null,null,null,null,null,null);
        return new UserBean();

    }
}
