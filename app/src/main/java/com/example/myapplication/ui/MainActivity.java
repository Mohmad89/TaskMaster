package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.myapplication.AppDatabase;
import com.example.myapplication.R;
import com.example.myapplication.RecyclerViewAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mAddTask, mAllTask;
    private TextView mTextUsername, mTotalTask;
    private Spinner mSpinner;
    private RecyclerView mRecyclerView;

    // List<Task> arrayFromRoom;

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
        mTextUsername = findViewById(R.id.text_username);
        mRecyclerView = findViewById(R.id.recycler_view);
        mTotalTask = findViewById(R.id.total_task);

        // Initialize Amplify
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.configure(getApplicationContext());
            Log.i(TAG, "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e(TAG, "Could not initialize Amplify", error);
        }

//        List<com.example.myapplication.Task> arrayList = new ArrayList<>();
//        arrayList.add(new com.example.myapplication.Task("Task1", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley", "new"));
//        arrayList.add(new com.example.myapplication.Task ("Task2", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley", "assigned"));
//        arrayList.add(new com.example.myapplication.Task ("Task3", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley", "In progress"));
//        arrayList.add(new com.example.myapplication.Task ("Task4", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley", "complete"));

//        arrayFromRoom = AppDatabase.getInstance(this).taskDao().getAllTasks();


        List <com.example.myapplication.Task> apiListArray = new ArrayList<>();
        Amplify.API.query(ModelQuery.list(Task.class),
                success-> {

                    for (Task task : success.getData()) {

//                        com.example.myapplication.Task newTask = new com.example.myapplication.Task(task.getTitle(), task.getBody(), task.getState());
                        apiListArray.add(new com.example.myapplication.Task(task.getTitle(), task.getBody(), task.getState()));
                    }
                    Log.i(TAG, "success " );
                },
                failure ->{
                    Log.i(TAG, "failure " + failure);
                });


        recyclerMethod(apiListArray);

        mAddTask.setOnClickListener(mAddTaskClick);
        mAllTask.setOnClickListener(mAllTaskClick);

        // syncs the local DataStore with the backend when changes to the local DataStore are detected
        Amplify.DataStore.observe(Task.class,
                started -> Log.i("Tutorial", "Observation began."),
                change -> Log.i("Tutorial", change.item().toString()),
                failure -> Log.e("Tutorial", "Observation failed.", failure),
                () -> Log.i("Tutorial", "Observation complete.")
        );
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

        List <com.example.myapplication.Task> apiListArray = new ArrayList<>();
        Amplify.API.query(ModelQuery.list(Task.class),
                success-> {

                    for (Task task : success.getData()) {
//                        com.example.myapplication.Task newTask = new com.example.myapplication.Task(task.getTitle(), task.getBody(), task.getState());
                        apiListArray.add(new com.example.myapplication.Task(task.getTitle(), task.getBody(), task.getState()));
                    }
                    Log.i(TAG, "success " );
                },
                failure ->{
                    Log.i(TAG, "failure " + failure);
                });


        recyclerMethod(apiListArray);
    }

    private void setUsername () {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mTextUsername.setText(sharedPreferences.getString("username", "No User Name Yet"));
    }

    public void recyclerMethod (List <com.example.myapplication.Task> array) {
        // create Adapter
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(array, new RecyclerViewAdapter.ClickListener() {
            @Override
            public void onTaskItemClicked(int position) {
                Toast.makeText(getApplicationContext(), position + " ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), TaskDetails.class);
                intent.putExtra("id", array.get(position).getId());
                startActivity(intent);
            }
        });
        // set adapter on recycler view
        mRecyclerView.setAdapter(recyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set other important properties
    }
}