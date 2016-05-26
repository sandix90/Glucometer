package org.sandix.glucometer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.sandix.glucometer.asyncTasks.AsyncDbExecutor;
import org.sandix.glucometer.beans.GlBean;
import org.sandix.glucometer.beans.UserBean;
import org.sandix.glucometer.interfaces.AsyncTaskCompleteListener;
import org.sandix.glucometer.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandakov.a on 18.04.2016.
 */
public class DB {
    private Context context;
    private  DBHelper mDbHelper;
    private SQLiteDatabase mDB;
    private Cursor c;

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
    public void addRecordToMainTable(String serial_number, String last_name, String first_name, String middle_name,
                                     int age, String therapy_type, String diabetic_type, String gender, String phone_num,
                                     String email, String comments){
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
        cv.put("diabetic_type",diabetic_type);
        cv.put("email",email);
        cv.put("comments", comments);
        cv.put("sync_time_stamp", tools.getTimeStamp());

        mDB.insert("main",null,cv);

    }

    /**
     * Can create UserBean object by user id with all user information inside.
     *
     * Returns null if ID does not exist in DB
     *
     * @param user_id User ID from DB
     * @return UserBean object
     *
     *
     */

    public UserBean getUserDataById(int user_id){
        UserBean userBean;
        List<GlBean> glBeanList;

        Cursor c = mDB.rawQuery("SELECT * FROM main WHERE id = "+user_id+";", null);
        if(c.moveToFirst()){
            userBean =  new UserBean(user_id, c.getString(c.getColumnIndex("serialnumber")),
                    c.getString(c.getColumnIndex("last_name")),
                    c.getString(c.getColumnIndex("first_name")),
                    c.getString(c.getColumnIndex("middle_name")),
                    c.getInt(c.getColumnIndex("age")),
                    c.getString(c.getColumnIndex("email")),
                    c.getString(c.getColumnIndex("therapy_type")),
                    c.getString(c.getColumnIndex("diabetic_type")),
                    c.getString(c.getColumnIndex("phone")),
                    c.getInt(c.getColumnIndex("gender")) == 1 ? "Мужской" : "Женский",
                    c.getString(c.getColumnIndex("comments"))); //Убрал из конструктора инициализацию glBeanList, потому что в базе может не быть значений. Сделал setter
        }
        else{
            return null; // Завершаем работу если пользователя с таким id не существует или ошибка БД.
        }

        String last5GLvalues = "SELECT * FROM main WHERE serial_number='"+userBean.getSerial_number()+"' LIMIT 5 OFFSET (" +
                "SELECT count(*) FROM main)-5 order by id DESC"; //5 последних значений
        c = mDB.rawQuery(last5GLvalues,null);
        if(c.moveToFirst()){
            glBeanList = new ArrayList<>(); //Инициализируем, если запрос вернул значения глюкометра
            do {
                glBeanList.add(new GlBean(c.getFloat(c.getColumnIndex("gluc_value")), c.getString(c.getColumnIndex("value_date"))));
            }while (c.moveToNext());

            userBean.setGlBeanList(glBeanList); //Добавлем список значений к информации пользователя
        }
        return userBean;
    }

    public Cursor stringQuery(String request){
        return mDB.rawQuery(request,null);
    }

    public Cursor getAllUsers(){

        return  mDB.query("main",null,null,null,null,null,null);
    }

    public List<GlBean> getGlBeansBySerial(String serial_number){

        List<GlBean> beanList = new ArrayList<GlBean>();
        String query = "SELECT * from values_table WHERE serial_num='"+ serial_number +"'";
        AsyncDbExecutor executor = new AsyncDbExecutor(context, query);

        executor.setOnTaskCompleteListener(new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int request_type) {
                c=(Cursor)result;
            }
        });
        executor.execute();

        if(c!=null && c.moveToFirst()){
            do{
                beanList.add(new GlBean(c.getDouble(c.getColumnIndex("gluc_value")),c.getString(c.getColumnIndex("value_date"))));
            }while(c.moveToNext());
            return  beanList;
        }
        return null;
    }

    public Cursor execute(String query){
        return mDB.rawQuery(query,null);
    }

    public UserBean checkIfExist(String serial_number){
        Cursor c = mDB.rawQuery("SELECT * from main WHERE serialnumber='"+serial_number+"';",null);
        if(c.moveToFirst()){
            return new UserBean(Integer.parseInt(c.getString(c.getColumnIndex("id"))),
                    c.getString(c.getColumnIndex("serialnumber")),
                    c.getString(c.getColumnIndex("last_name")),
                    c.getString(c.getColumnIndex("first_name")),
                    c.getString(c.getColumnIndex("middle_name")),
                    c.getInt(c.getColumnIndex("age")),
                    c.getString(c.getColumnIndex("email")),
                    c.getString(c.getColumnIndex("therapy_type")),
                    c.getString(c.getColumnIndex("diabetic_type")),
                    c.getString(c.getColumnIndex("phone")),
                    c.getInt(c.getColumnIndex("gender")) == 1 ? "Мужской" : "Женский",
                    c.getString(c.getColumnIndex("comments"))); //Убрал из конструктора инициализацию glBeanList, потому что в базе может не быть значений. Сделал setter
        }
        else{
            return null; // Завершаем работу если пользователя с таким id не существует или ошибка БД.
        }
    }

    public void addValueToGlValuesTable(String serial_number, GlBean bean){
        ContentValues cv = new ContentValues();
        cv.put("serial_num", serial_number);
        cv.put("gluc_value", bean.getGl_value());
        cv.put("value_date", bean.getDate());
        mDB.insert("values_table",null,cv);
    }
}
