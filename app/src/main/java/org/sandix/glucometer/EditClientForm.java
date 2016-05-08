package org.sandix.glucometer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import org.sandix.glucometer.db.DB;


public class EditClientForm extends AppCompatActivity {
    private EditText firstNameEt,lastNameEt,middleNameEt, age, emailEt,phoneNumEt,serialNumberEt,commentsEt;
    private AppCompatSpinner genderSpinner,therapySpinner, diabetSpinner;
    private boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clientform);

        firstNameEt = (EditText) findViewById(R.id.first_name);
        lastNameEt = (EditText) findViewById(R.id.last_name);
        middleNameEt = (EditText) findViewById(R.id.middle_name);
        serialNumberEt = (EditText)findViewById(R.id.serial_number);
        commentsEt = (EditText)findViewById(R.id.comments);
        age = (EditText)findViewById(R.id.age);
        emailEt = (EditText)findViewById(R.id.email);
        phoneNumEt = (EditText)findViewById(R.id.phone_num);
        genderSpinner = (AppCompatSpinner) findViewById(R.id.gender_spinner);
        therapySpinner = (AppCompatSpinner) findViewById(R.id.therapy_spinner);
        diabetSpinner = (AppCompatSpinner) findViewById(R.id.diabetic_type_spinner);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Создание данных клиента");

        genderSpinner.setAdapter(new ArrayAdapter<>(this,R.layout.spinner_row,R.id.tv_item,getResources().getStringArray(R.array.gender_list))) ;
        therapySpinner.setAdapter(new ArrayAdapter<>(this,R.layout.spinner_row, R.id.tv_item,getResources().getStringArray(R.array.therapy_type)));
        diabetSpinner.setAdapter(new ArrayAdapter<>(this,R.layout.spinner_row, R.id.tv_item,getResources().getStringArray(R.array.diabets_type)));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.ok:
                if(!isOpen){
                makeUserRecord();
                }
                //Toast.makeText(EditClientForm.this, "user created", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeUserRecord() {

        String last_name;
        if(lastNameEt.getText().toString().equals("") ||
                firstNameEt.getText().toString().equals("") ||
                serialNumberEt.getText().toString().equals("") ||
                age.getText().toString().contentEquals("")){
            Toast.makeText(EditClientForm.this, "Проверьте заполнение обязательных полей ", Toast.LENGTH_SHORT).show();
            return;
        }
        DB db = new DB(this);
        db.open();
        db.addRecordToMainTable(serialNumberEt.getText().toString(),
                lastNameEt.getText().toString(),
                firstNameEt.getText().toString(),
                middleNameEt.getText().toString(),
                Integer.parseInt(age.getText().toString()),
                therapySpinner.getSelectedItem().toString(),
                diabetSpinner.getSelectedItem().toString(),
                genderSpinner.getSelectedItem().toString(),
                phoneNumEt.getText().toString(),
                emailEt.getText().toString(),
                commentsEt.getText().toString());
        db.close();
        Toast.makeText(EditClientForm.this, "Запись успешно добавлена", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_clientform_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }
}
