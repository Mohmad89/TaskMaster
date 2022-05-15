package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

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
        long id =  intent.getLongExtra("id", 0);
        Task task = AppDatabase.getInstance(this).taskDao().getTask(id);

        mTaskTitle.setText(task.getTitle());
        mTaskBody.setText(task.getBody());
        mTaskState.setText(task.getState());

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