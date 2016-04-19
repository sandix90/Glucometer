package org.sandix.glucometer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.sandix.glucometer.adapters.UserInfoAdapter;
import org.sandix.glucometer.beans.Bean;

import org.sandix.glucometer.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandakov.a on 12.04.2016.
 */
public class ClientForm extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    RecyclerView mainRecyclerView;
    DBHelper dbHelper;
    SQLiteDatabase db;
    int user_id;
    Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientform);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainRecyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        //TODO: need to receive user_id from intent
        user_id =0;
        context = this;
        //getSupportLoaderManager().initLoader(0,null,this);
        DbAsyncTaskLoader taskLoader = new DbAsyncTaskLoader(this);
        taskLoader.registerListener(0, new android.content.Loader.OnLoadCompleteListener<List<Bean>>() {
            @Override
            public void onLoadComplete(android.content.Loader<List<Bean>> loader, List<Bean> data) {
                mainRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                UserInfoAdapter adapter = new UserInfoAdapter(context, data);
                mainRecyclerView.setAdapter(adapter);
            }
        });
        taskLoader.startLoading();

//        DB mdb = new DB(this);
//        mdb.open();
//        mdb.addRecord();
//        mdb.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this,db,user_id);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<String> mDatas = new ArrayList<>();
        if(data.moveToFirst()){
            do{

                mDatas.add(data.getString(data.getColumnIndex("phone")));
                mDatas.add(data.getString(data.getColumnIndex("email")));
            }while(data.moveToNext());
        }

        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //UserInfoAdapter adapter = new UserInfoAdapter(this,mDatas);
        //mainRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    static class MyCursorLoader extends CursorLoader{
        SQLiteDatabase db;
        int user_id;

        public MyCursorLoader(Context context, SQLiteDatabase db, int user_id){
            super(context);
            this.db = db;
            this.user_id = user_id;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor c = db.query("main",null,null,null,null,null,null);
            return c;
        }
    }


}
