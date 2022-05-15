package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class AddTask extends AppCompatActivity {

    TextView mTotalTask;
    EditText mTaskName, mTaskDescription;
    Button mAddTask;

    View.OnClickListener mAddTaskClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(AddTask.this, "Submitted!", Toast.LENGTH_SHORT).show();

            // get the value from editText Fields
            String taskName = mTaskName.getText().toString();
            String taskDesc = mTaskDescription.getText().toString();

            // Task Instance
            Task task = new Task(taskName, taskDesc, "new");

            // Save task object inside AppDatabase (RoomDatabase)
            AppDatabase.getInstance(getApplicationContext()).taskDao().insertAll(task);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        //Inflate
        mAddTask = findViewById(R.id.submit_task);
        mTaskName = findViewById(R.id.task_name);
        mTaskDescription = findViewById(R.id.task_description);
        mTotalTask = findViewById(R.id.total_task);

        mAddTask.setOnClickListener(mAddTaskClick);

        List<Task> totalTask = AppDatabase.getInstance(this).taskDao().getAllTasks();
        mTotalTask.setText(totalTask.size()+ "");

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