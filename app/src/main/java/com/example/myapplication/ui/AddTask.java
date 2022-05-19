package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.myapplication.AppDatabase;
import com.example.myapplication.R;

import java.util.List;

public class AddTask extends AppCompatActivity {

    private static final String TAG = AddTask.class.getSimpleName();
    private TextView mTotalTask;
    private EditText mTaskName, mTaskDescription;
    private Button mAddTask;
    private Spinner mSpinnerState;
    private String state = "";



    View.OnClickListener mAddTaskClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            Toast.makeText(AddTask.this, "Submitted!", Toast.LENGTH_SHORT).show();

            // get the value from editText Fields
            String taskName = mTaskName.getText().toString();
            String taskDesc = mTaskDescription.getText().toString();

            // Task Instance
//            Task task = new Task(taskName, taskDesc, state);

            // Save task object inside AppDatabase (RoomDatabase)
//            AppDatabase.getInstance(getApplicationContext()).taskDao().insertAll(task);

            // Save Task properties inside The Dynamo Database
            Task newTask = Task.builder()// this Task from com.amplifyframework.datastore.generated.model.Task
                    .title(taskName)
                    .body(taskDesc)
                    .state(state)
                    .build(); // we must to write it to build the object

            Amplify.DataStore.save(
                    newTask,
                    success -> {
                        Log.i(TAG, "We were successful => " + success);
                    },
                    error -> {
                        Log.e(TAG, "We got an error => " + error );
                    });

            // Save to my local machine
            Amplify.DataStore.query(
                    Task.class,
                    tasks -> {
                        while (tasks.hasNext()){
                            Task task = tasks.next();
                            Log.i(TAG, "Tasks is => " + task);
                        }
                    },
                    error -> {
                        Log.i(TAG, "Error" + error);
                    }
                    );

            // Save to the backend
            Amplify.API.mutate(ModelMutation.create(newTask),
                    success -> {},
                    error -> {});
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
        mSpinnerState = findViewById(R.id.spinner);



        mSpinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String selectedItem = adapterView.getItemAtPosition(position).toString();
                switch (selectedItem) {
                    case "New" :
                        state = "New";
                        break;
                    case "Submitted":
                        state = "Submitted";
                        break;
                    case "InProgress":
                        state = "InProgress";
                        break;
                    case "Completed":
                        state = "Completed";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        mAddTask.setOnClickListener(mAddTaskClick);

//        List<Task> totalTask = AppDatabase.getInstance(this).taskDao().getAllTasks();
        mTotalTask.setText("");


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