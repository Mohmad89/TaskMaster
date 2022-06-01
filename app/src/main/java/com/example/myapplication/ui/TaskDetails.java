package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

public class TaskDetails extends AppCompatActivity {

    private static final String TAG = TaskDetails.class.getSimpleName();
    private TextView mTaskTitle, mTaskBody, mTaskState;
    private ImageView mTaskImage;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // Inflate
        mTaskTitle = findViewById(R.id.text_task_name);
        mTaskBody  = findViewById(R.id.text_task_body);
        mTaskState = findViewById(R.id.text_task_state);
        mTaskImage = findViewById(R.id.task_image);

        //Get Value from Intent
        Intent intent = getIntent();
        String title =  intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String state = intent.getStringExtra("state");

        String key = intent.getStringExtra("imageKey");
        String url = "";
        Log.i(TAG, "key"+ key);
        Amplify.Storage.getUrl(
                key+".jpg",
                success ->{
                    Bundle bundle = new Bundle();
                    bundle.putString("url", success.getUrl().toString());

                    Message message = new Message();
                    message.setData(bundle);

                    handler.sendMessage(message);

                    Log.i(TAG, "image " + success.getUrl());
                },
                error -> {
                    Log.i(TAG, "image Error : " + error);
                });

//        Task task = AppDatabase.getInstance(this).taskDao().getTask(id);


        mTaskTitle.setText(title);
        mTaskBody.setText(description);
        mTaskState.setText(state);

        // Add Back Button in ActionBar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Handler
        handler = new Handler(Looper.getMainLooper(), msg -> {
            String imageUrl = msg.getData().getString("url");
            Picasso.get().load(imageUrl).into(mTaskImage);
            return true;
        });
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