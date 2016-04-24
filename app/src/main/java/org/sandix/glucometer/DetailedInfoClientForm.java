package org.sandix.glucometer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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

import org.sandix.glucometer.beans.UserBean;
import org.sandix.glucometer.db.DB;
import org.sandix.glucometer.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandakov.a on 12.04.2016.
 */
public class DetailedInfoClientForm extends AppCompatActivity {

    RecyclerView mainRecyclerView;
    Context context;
    int id_to_open;


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
        context = this;
        if(getIntent().hasExtra("user_id")){
            id_to_open = getIntent().getIntExtra("user_id",-1);
            openUser();

        }

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
    protected void onDestroy() {
        super.onDestroy();
    }

    private void openUser() {
        if(id_to_open!=-1){
            AsyncTask task = new AsyncDbRequest(context);
            task.execute(id_to_open);
        }

    }




    class AsyncDbRequest extends AsyncTask<Integer,Void,UserBean>{
        private Context context;
        public AsyncDbRequest(Context context){
            this.context = context;
        }

        @Override
        protected UserBean doInBackground(Integer... params) {
            DB db = new DB(context);
            db.open();

            return null;
        }
    }


}
