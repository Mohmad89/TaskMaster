package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TaskDetails extends AppCompatActivity {

    private static final String TAG = TaskDetails.class.getSimpleName();
    private TextView mTaskTitle, mTaskBody, mTaskState, mLongitude, mLatitude;
    private ImageView mTaskImage;
    private Handler handler;
    private FloatingActionButton mSpeechButton;

    private final MediaPlayer mp = new MediaPlayer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // Inflate
        mTaskTitle = findViewById(R.id.text_task_name);
        mTaskBody  = findViewById(R.id.text_task_body);
        mTaskState = findViewById(R.id.text_task_state);
        mTaskImage = findViewById(R.id.task_image);
        mLongitude = findViewById(R.id.text_task_long);
        mLatitude  = findViewById(R.id.text_task_lat);
        mSpeechButton = findViewById(R.id.floating_action_button);

        //Get Value from Intent
        Intent intent = getIntent();
        String title =  intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String state = intent.getStringExtra("state");
        String longValue = intent.getStringExtra("longitude");
        String latValue = intent.getStringExtra("latitude");

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
        mLongitude.setText("Long : " + longValue);
        mLatitude.setText("Lat  : " + latValue);


        mSpeechButton.setOnClickListener(view -> {
            Amplify.Predictions.convertTextToSpeech(
                    mTaskBody.getText().toString(),
                    result -> playAudio(result.getAudioData()),
                    error -> Log.e("MyAmplifyApp", "Conversion failed", error)
            );
        });

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

    private void playAudio(InputStream data) {
        File mp3File = new File(getCacheDir(), "audio.mp3");

        try (OutputStream out = new FileOutputStream(mp3File)) {
            byte[] buffer = new byte[8 * 1_024];
            int bytesRead;
            while ((bytesRead = data.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            mp.reset();
            mp.setOnPreparedListener(MediaPlayer::start);
            mp.setDataSource(new FileInputStream(mp3File).getFD());
            mp.prepareAsync();
        } catch (IOException error) {
            Log.e("MyAmplifyApp", "Error writing audio file", error);
        }
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