package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.myapplication.R;
import com.example.myapplication.Adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mAddTask, mAllTask;
    private TextView mTextUsername, mTextTeam;
    private RecyclerView mRecyclerView;
    private Handler handler;

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
        mTextTeam = findViewById(R.id.text_team);

        // Initialize Amplify
        


//        List<com.example.myapplication.data.Task> arrayList = new ArrayList<>();
//        arrayList.add(new com.example.myapplication.data.Task("Task1", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley", "new"));
//        arrayList.add(new com.example.myapplication.data.Task ("Task2", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley", "assigned"));
//        arrayList.add(new com.example.myapplication.data.Task ("Task3", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley", "In progress"));
//        arrayList.add(new com.example.myapplication.data.Task ("Task4", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley", "complete"));

//        arrayFromRoom = AppDatabase.getInstance(this).taskDao().getAllTasks();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String teamTitle = sharedPreferences.getString("teamTitle", "All Team");

        if (teamTitle != "All Team"){
            displaySelectedTeam(teamTitle);
        } else {
            displayAllTeam(teamTitle);
        }


        mAddTask.setOnClickListener(mAddTaskClick);
        mAllTask.setOnClickListener(mAllTaskClick);

        DataStoreSync();
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
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        Amplify.Auth.signOut(
                ()-> {
                    startActivity(new Intent(MainActivity.this, Login.class));
                    authSession("logout");
                    finish();
                },
                error ->{

                });
    }

    private void authSession (String method){
        Amplify.Auth.fetchAuthSession(
                result ->{},
                error->{});
    }

    public void DataStoreSync () {
        // syncs the local DataStore with the backend when changes to the local DataStore are detected
        Amplify.DataStore.observe(Task.class,
                started ->{},
                change -> {},
                failure -> {},
                () -> {}
        );
    }

    public void menuToSetting(){
        Intent newIntent  = new Intent(getApplicationContext(), Setting.class);
        startActivity(newIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUsername();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String teamTitle = sharedPreferences.getString("teamTitle", "All Team");

        if (teamTitle != "All Team"){
            displaySelectedTeam(teamTitle);
        } else {
            displayAllTeam(teamTitle);
        }
    }

    private void setUsername () {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mTextUsername.setText(sharedPreferences.getString("username",""));
    }

    public void displayAllTeam(String teamTitle){
        List<com.example.myapplication.data.Task> apiListArray = new ArrayList<>();
        Amplify.API.query(ModelQuery.list(Task.class),
                success -> {
                    for (Task task : success.getData()) {
                        apiListArray.add(new com.example.myapplication.data.Task(task.getTitle(), task.getBody(), task.getState(), task.getImageKey()));
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("true", "true");

                    Message message = new Message();
                    message.setData(bundle);

                    handler.sendMessage(message);
                }, error -> {}
        );
        // Handler
        handler = new Handler(Looper.getMainLooper(), msg -> {
            String text = msg.getData().getString("true");
            recyclerMethod(apiListArray);
            return true;
        });
        mTextTeam.setText(teamTitle);

    }

    public void displaySelectedTeam(String teamTitle){

        ArrayList<com.example.myapplication.data.Task> arrayList = new ArrayList<>();
        final String[] id = new String[1];
        Amplify.API.query(ModelQuery
                .list(Team.class, Team.TITLE.eq(teamTitle)),
                teamSuccess -> {
                    for (Team team: teamSuccess.getData()){
                        id[0] = team.getId();
                    }
                    Amplify.API.query(ModelQuery.list(
                            Task.class,
                            Task.TEAM_TASKS_ID.eq(id[0])),
                            success -> {
                                for (Task task : success.getData()){
                                    arrayList.add(new com.example.myapplication.data.Task(task.getTitle(), task.getBody(), task.getState(), task.getImageKey()));
                                }
                                Bundle bundle = new Bundle();
                                bundle.putString("true", "true");

                                Message message = new Message();
                                message.setData(bundle);

                                handler.sendMessage(message);
                            },
                            error -> {
                                Log.e(TAG, "error ", error);
                            });

                },
                error -> {
                    Log.e(TAG, "error ", error);
                });
        
        // Handler
        handler = new Handler(Looper.getMainLooper(), msg -> {
            String text = msg.getData().getString("true");
            recyclerMethod(arrayList);
            return true;
        });
        mTextTeam.setText(teamTitle);

    }

    public void recyclerMethod (List <com.example.myapplication.data.Task> array) {
        // create Adapter
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(array, new RecyclerViewAdapter.ClickListener() {
            @Override
            public void onTaskItemClicked(int position) {
                Toast.makeText(getApplicationContext(), position + " ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), TaskDetails.class);
                intent.putExtra("title", array.get(position).getTitle());
                intent.putExtra("description", array.get(position).getBody());
                intent.putExtra("state", array.get(position).getState());
                intent.putExtra("imageKey", array.get(position).getImageKey());
                startActivity(intent);
            }
        });
        // set adapter on recycler view
        mRecyclerView.setAdapter(recyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set other important properties
    }



}