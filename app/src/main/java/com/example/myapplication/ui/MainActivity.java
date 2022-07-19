package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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
import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.myapplication.R;
import com.example.myapplication.Adapter.RecyclerViewAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mAddTask, mAllTask;
    private TextView mTextUsername, mTextTeam, mTextScore;
    private RecyclerView mRecyclerView;
    private Handler handler;
    private FloatingActionButton mInterstitialButton, mRewardedButton;
    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;


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
        inflateView();


        // load ad into the AdView
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // load the interstitial ad
        loadInterstitialAd();

        mInterstitialButton.setOnClickListener(view ->{
        if (mInterstitialAd != null) {
            mInterstitialAd.show(MainActivity.this);
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.");
        }
        });


        // load the rewarded ad
        loadRewardedAd();
        mRewardedButton.setOnClickListener(view -> {
            if (mRewardedAd != null) {
                Activity activityContext = MainActivity.this;
                mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {

                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d(TAG, "The user earned the reward.");
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                        Toast.makeText(activityContext, "the amount => " + rewardAmount, Toast.LENGTH_SHORT).show();

                    }
                });
            } else {
                Log.d(TAG, "The rewarded ad wasn't ready yet.");
            }
            mTextScore.setText("5");
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String teamTitle = sharedPreferences.getString("teamTitle", "All Team");

        if (teamTitle != "All Team"){
            displaySelectedTeam(teamTitle);
        } else {
            displayAllTeam(teamTitle);
        }


        mAddTask.setOnClickListener(mAddTaskClick);
        mAllTask.setOnClickListener(mAllTaskClick);

        handler = new Handler(Looper.getMainLooper(), msg -> {
            mTextScore.setText("5");
            return true;
        });
        mTextTeam.setText(teamTitle);

        DataStoreSync();
        // Add Back Button in ActionBar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void inflateView() {
        mAddTask = findViewById(R.id.add_task);
        mAllTask = findViewById(R.id.all_task);
        mTextUsername = findViewById(R.id.text_username);
        mRecyclerView = findViewById(R.id.recycler_view);
        mTextTeam = findViewById(R.id.text_team);
        mInterstitialButton = findViewById(R.id.interstitial_adv_button);
        mRewardedButton = findViewById(R.id.reward_adv_button);
        mTextScore = findViewById(R.id.score_text);
    }

    private void initialisedAdv(){
        // needs to be initialised once
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.i(TAG, "onInitializationComplete: AD-MOB INITIALIZED");
            }
        });
    }

    private void recordEvent() {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("openedMyApp")
                .addProperty("Successful", true)
                .addProperty("ProcessDuration", 792)
                .build();

        Amplify.Analytics.recordEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_share).setVisible(false);
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
            case R.id.action_profile:
                profile();

        }
        return true;
    }

    private void profile() {
        startActivity(new Intent(getApplicationContext(), Profile.class));
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
                        apiListArray.add(new com.example.myapplication.data.Task(task.getTitle(), task.getBody(), task.getState(), task.getImageKey(), task.getLongitude(), task.getLatitude()));
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
                                    arrayList.add(new com.example.myapplication.data.Task(task.getTitle(), task.getBody(), task.getState(), task.getImageKey(), task.getLongitude(), task.getLatitude()));
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
                intent.putExtra("latitude", array.get(position).getLatitude());
                intent.putExtra("longitude", array.get(position).getLongitude());
                startActivity(intent);
            }
        });
        // set adapter on recycler view
        mRecyclerView.setAdapter(recyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set other important properties
    }

    // Interstitial Adv
    private void loadInterstitialAd() {
        Log.i(TAG, "loadInterstitialAd: ");
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        MainActivity.this.mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    // Rewarded Adv
    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d(TAG, "Ad was loaded.");

                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad was shown.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.d(TAG, "Ad failed to show.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad was dismissed.");
                                mRewardedAd = null;
                            }
                        });
                    }
                });
    }



}