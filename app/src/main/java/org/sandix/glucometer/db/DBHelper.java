package org.sandix.glucometer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alex on 11.04.2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context){
        super(context,"glucometer_db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String main_tableQuery=
                "CREATE TABLE main (" +
                        "id integer primary key autoincrement," +
                        "serialnumber nvarchar(30)," +
                        "last_name nvarchar(50)," +
                        "middle_name nvarchar(50)," +
                        "first_name nvarchar(50)," +
                        "age integer," +
                        "therapy_type nvarchar(50)" +
                        "sex boolean" +
                        ")";
        db.execSQL(main_tableQuery);


        String values_tableQuery=
                "CREATE TABLE values (" +
                        "id integer primary key autoincrement," +
                        "serial_num nvarchar(30)," +
                        "gluc_value real," +
                        "value_date datetime)";
        db.execSQL(values_tableQuery);

        String sync_tableQuery =
                "CREATE TABLE sync (" +
                        "id integer primary key autoincrement," +
                        "serial_num nvarchar(30)," +
                        "last_sync datetime)";
        db.execSQL(sync_tableQuery);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
