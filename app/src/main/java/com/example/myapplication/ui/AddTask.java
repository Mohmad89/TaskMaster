package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class AddTask extends AppCompatActivity {

    private static final String TAG = AddTask.class.getSimpleName();
    private TextView mTotalTask;
    private EditText mTaskName, mTaskDescription;
    private Button mAddTask;
    private AutoCompleteTextView mAutoCompletedState, mAutoCompletedTeam;
    private String state, teamTitle;
    private Handler handler;

    private String [] teamItem = {"Team1", "Team2", "Team3"};
    private String [] stateItem = {"New", "Submitted", "InProgress", "Completed"};
    private ArrayAdapter<String> adapterTeam, adapterState;




    View.OnClickListener mAddTaskClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(AddTask.this, "Submitted!", Toast.LENGTH_SHORT).show();

            // get the value from editText Fields
            String taskName = mTaskName.getText().toString();
            String taskDesc = mTaskDescription.getText().toString();

            // Task Instance
//            Task task = new Task(taskName, taskDesc, state);

            // Save task object inside AppDatabase (RoomDatabase)
//            AppDatabase.getInstance(getApplicationContext()).taskDao().insertAll(task);

            // Get The Id For Selected Team
            Amplify.API.query(
                    ModelQuery.list(Team.class, Team.TITLE.eq(teamTitle)),
                    teamSuccess -> {
                        for (Team team: teamSuccess.getData()) {
                                Task newTask = Task.builder()
                                        .title(taskName)
                                        .body(taskDesc)
                                        .state(state)
                                        .teamTasksId(team.getId())
                                        .build(); // we must to write it to build the object

                                Amplify.DataStore.save(
                                        newTask,
                                        success -> {
                                        },
                                        error -> {
                                        });

                                // Save to the backend
                                Amplify.API.mutate(ModelMutation.create(newTask),
                                        success -> {
                                        },
                                        error -> {
                                        });
                            }
                    },
                    error -> {
                    }
            );

            mTaskName.setText("");
            mTaskDescription.setText("");
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
        mAutoCompletedState = findViewById(R.id.spinner);
        mAutoCompletedTeam = findViewById(R.id.team_spinner);

        adapterTeam = new ArrayAdapter<>(this, R.layout.list_item, teamItem);
        mAutoCompletedTeam.setAdapter(adapterTeam);

        adapterState = new ArrayAdapter<>(this, R.layout.list_item, stateItem);
        mAutoCompletedState.setAdapter(adapterState);


        //state spinner
        mAutoCompletedState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

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
        });

        // team spinner
        ArrayList<String> arrayList = new ArrayList<>();

        Amplify.API.query(ModelQuery.list(Team.class),
                success ->{
                    for (Team team : success.getData()){
                        arrayList.add(team.getTitle());
                    }
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("TEAM_LIST",arrayList);

                    Message message = new Message();
                    message.setData(bundle);
                    handler.sendMessage(message);
                },
                error ->{
                });

        // Team Spinner
        mAutoCompletedTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();

                switch (itemSelected){
                    case "Team1":
                        teamTitle = "Team1";
                        break;
                    case "Team2" :
                        teamTitle = "Team2";
                        break;
                    case "Team3" :
                        teamTitle = "Team3";
                        break;
                }

            }

        });


        mAddTask.setOnClickListener(mAddTaskClick);

//        List<Task> totalTask = AppDatabase.getInstance(this).taskDao().getAllTasks();

        mTotalTask.setText("");

        // Handler
        handler = new Handler(Looper.getMainLooper(), msg -> {
            ArrayList <String> array = msg.getData().getStringArrayList("TEAM_LIST");
            teamSpinner(array);
            return true;
        });

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

    public void teamSpinner (ArrayList<String> array) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAutoCompletedTeam.setAdapter(arrayAdapter);
        mAutoCompletedTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                switch (selectedItem){
                    case "Team1":
                        teamTitle = "Team1";
                        break;
                    case "Team2" :
                        teamTitle = "Team2";
                        break;
                    case "Team3" :
                        teamTitle = "Team3";
                        break;
                }

            }
        });
    }
}