package org.sandix.glucometer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sandix.glucometer.adapters.UserInfoAdapter;
import org.sandix.glucometer.asyncTasks.AsyncDbExecutor;
import org.sandix.glucometer.beans.Bean;

import org.sandix.glucometer.beans.UserBean;
import org.sandix.glucometer.db.DB;
import org.sandix.glucometer.db.DBHelper;
import org.sandix.glucometer.interfaces.AsyncTaskCompleteListener;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by sandakov.a on 12.04.2016.
 */
public class DetailedInfoClientForm extends AppCompatActivity implements View.OnClickListener {


    Context context;
    TextView diabeticTypeTv,therapyTypeTv,ageTv,emailTv,phoneTv,commentsTv,genderTv, patientLastNameTv,patientFirstNameTv;
    UserBean selectedUserBean;
    FloatingActionButton edit_btn;
    LinearLayout gl_values_ll;
    //int id_to_open;


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

        diabeticTypeTv = (TextView) findViewById(R.id.diabetic_type);
        therapyTypeTv = (TextView) findViewById(R.id.therapy_type);
        ageTv = (TextView)findViewById(R.id.age);
        emailTv = (TextView)findViewById(R.id.email);
        phoneTv = (TextView)findViewById(R.id.phone);
        commentsTv = (TextView)findViewById(R.id.comments);
        genderTv = (TextView)findViewById(R.id.gender);
        patientLastNameTv = (TextView)findViewById(R.id.patient_last_name);
        patientFirstNameTv = (TextView)findViewById(R.id.patient_first_name);
        edit_btn = (FloatingActionButton)findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(this);
        gl_values_ll = (LinearLayout) findViewById(R.id.gl_values_ll);
        gl_values_ll.setVisibility(View.INVISIBLE);


        context = this;

        if(getIntent().hasExtra("userBean")){
            selectedUserBean = (UserBean)getIntent().getSerializableExtra("userBean");
            openUser();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailed_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.show_chart:
                Intent intent = new Intent(this,ActivityChart.class);
                intent.putExtra("userBean", selectedUserBean);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void openUser() {
        patientLastNameTv.setText(selectedUserBean.getLast_name());
        patientFirstNameTv.setText(selectedUserBean.getFirst_name());
        diabeticTypeTv.setText(selectedUserBean.getDiabetic_type());
        therapyTypeTv.setText(selectedUserBean.getTherapy_type());
        ageTv.setText(String.valueOf(selectedUserBean.getAge()));
        emailTv.setText(selectedUserBean.getEmail());
        phoneTv.setText(selectedUserBean.getPhone());
        genderTv.setText(selectedUserBean.getGender());
        commentsTv.setText(selectedUserBean.getComments());

        //DB db = new DB(this);
        //db.getUserDataById(selectedUserBean.getId());
//        String query = "SELECT * FROM main WHERE serial_number='"+selectedUserBean.getSerial_number()+"' LIMIT 5 OFFSET (" +
//                "SELECT count(id) FROM main )-5 order by id DESC;";
        String query = "SELECT * FROM values_table WHERE serial_num='"+selectedUserBean.getSerial_number()+"' ORDER BY value_date LIMIT 5 ";

        AsyncDbExecutor executor = new AsyncDbExecutor(this,query);
        executor.execute();
        executor.setOnTaskCompleteListener(new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int request_type) {
                if(result!=null) {
                    gl_values_ll.setVisibility(View.VISIBLE);
                    Cursor c = (Cursor) result;
                    if(c.moveToFirst()){
                        do{
                            View  v = getLayoutInflater().inflate(R.layout.row_glucometer_value,gl_values_ll,false);
                            TextView gluc_value = (TextView) v.findViewById(R.id.gluc_value);
                            TextView value_date = (TextView)v.findViewById(R.id.value_date);
                            gluc_value.setText(c.getString(c.getColumnIndex("gluc_value")));
                            value_date.setText(c.getString(c.getColumnIndex("value_date")));
                            gl_values_ll.addView(v);
                        }while(c.moveToNext());
                    }
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.edit_btn:
                Snackbar.make(findViewById(R.id.coordinator_layout),"Ok, edit it",Snackbar.LENGTH_SHORT).show();
                break;
        }
    }
}
