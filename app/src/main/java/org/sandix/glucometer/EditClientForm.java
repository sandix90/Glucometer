package org.sandix.glucometer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;


public class EditClientForm extends AppCompatActivity {
    private EditText firstName,lastName,middleName;
    private AppCompatSpinner genderSpinner,therapySpinner, diabetsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clientform);

        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        middleName = (EditText) findViewById(R.id.middle_name);
        genderSpinner = (AppCompatSpinner) findViewById(R.id.gender_spinner);
        therapySpinner = (AppCompatSpinner) findViewById(R.id.therapy_spinner);
        diabetsSpinner = (AppCompatSpinner) findViewById(R.id.diabets_spinner);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Создание данных клиента");

        genderSpinner.setAdapter(new ArrayAdapter<>(this,R.layout.spinner_row,R.id.tv_item,getResources().getStringArray(R.array.gender_list))) ;
        therapySpinner.setAdapter(new ArrayAdapter<>(this,R.layout.spinner_row, R.id.tv_item,getResources().getStringArray(R.array.therapy_type)));
        diabetsSpinner.setAdapter(new ArrayAdapter<>(this,R.layout.spinner_row, R.id.tv_item,getResources().getStringArray(R.array.diabets_type)));


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.ok:
                Toast.makeText(EditClientForm.this, "user created", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_clientform_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }
}
