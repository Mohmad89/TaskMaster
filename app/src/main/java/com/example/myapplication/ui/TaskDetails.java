package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.myapplication.AppDatabase;
import com.example.myapplication.R;
import com.example.myapplication.Task;

public class TaskDetails extends AppCompatActivity {
    TextView mTaskTitle, mTaskBody, mTaskState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        mTaskTitle = findViewById(R.id.text_task_name);
        mTaskBody  = findViewById(R.id.text_task_body);
        mTaskState = findViewById(R.id.text_task_state);

        //Get Value from Intent
        Intent intent = getIntent();
        String title =  intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String state = intent.getStringExtra("state");

//        Task task = AppDatabase.getInstance(this).taskDao().getTask(id);

        mTaskTitle.setText(title);
        mTaskBody.setText(description);
        mTaskState.setText(state);

        // Add Back Button in ActionBar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}