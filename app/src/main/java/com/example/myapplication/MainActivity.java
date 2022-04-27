package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private Button mAddTask, mAllTask, mTask1, mTask2, mTask3;
    private TextView mTextUsername;
    private Spinner mSpinner;


    // Add Task Click To move For AddTask Page
    private final View.OnClickListener mAddTaskClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent addTaskPage = new Intent(getApplicationContext(), AddTask.class);
            startActivity(addTaskPage);
        }
    };

    // All Task Click To Move For AllTask Page
    private final View.OnClickListener mAllTaskClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent allTaskPage = new Intent(getApplicationContext(), AllTask.class);
            startActivity(allTaskPage);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inflate
        mAddTask = findViewById(R.id.add_task);
        mAllTask = findViewById(R.id.all_task);
//        mTask1 = findViewById(R.id.task1);
//        mTask2 = findViewById(R.id.task2);
//        mTask3 = findViewById(R.id.task3);
        mTextUsername = findViewById(R.id.text_username);
        mSpinner = findViewById(R.id.spinner);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedItem = adapterView.getItemAtPosition(i).toString();
                Intent intent = new Intent(getApplicationContext(), TaskDetails.class);
                switch (selectedItem){
                    case "FrontEnd Task" :
                        intent.putExtra("task", "FrontEnd Task");
                        startActivity(intent);
                        break;
                    case "BackEnd Task" :
                        intent.putExtra("task", "BackEnd Task");
                        startActivity(intent);
                        break;
                    case "Database Task" :
                        intent.putExtra("task", "Database Task");
                        startActivity(intent);
                        break;
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mAddTask.setOnClickListener(mAddTaskClick);
        mAllTask.setOnClickListener(mAllTaskClick);

        // Add Back Button in ActionBar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_setting:
                menuToSetting();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void menuToSetting(){
        Intent newIntent  = new Intent(getApplicationContext(), Setting.class);
        startActivity(newIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUsername();
    }

    private void setUsername () {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mTextUsername.setText(sharedPreferences.getString("username", "No User Name Yet"));
    }
}