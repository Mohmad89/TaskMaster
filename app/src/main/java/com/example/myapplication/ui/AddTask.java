package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class AddTask extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = AddTask.class.getSimpleName();
    public static final int REQUEST_CODE = 123;
    private TextView mTotalTask;
    private EditText mTaskName, mTaskDescription;
    private Button mAddTask, mUploadBtn;
    private AutoCompleteTextView mAutoCompletedState, mAutoCompletedTeam;
    private String state, teamTitle, imageKey;
    private Handler handler;

    private String [] teamItem = {"Team1", "Team2", "Team3"};
    private String [] stateItem = {"New", "Submitted", "InProgress", "Completed"};
    private ArrayAdapter<String> adapterTeam, adapterState;

    private String longValue, latValue;

    private FusedLocationProviderClient mFusedLocationClient;

    private int PERMISSION_ID = 44;

    private double latitude;
    private double longitude;

    private File file;

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            longValue = mLastLocation.getLongitude() + "";
            latValue = mLastLocation.getLatitude() + "";
            Log.i(TAG, "long :" + longValue + " Lat : "+ latValue);
        }
    };



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
                                        .imageKey(imageKey)
                                        .longitude(longitude + "")
                                        .latitude(latitude + "")
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
        mUploadBtn = findViewById(R.id.btn_upload);

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        // method to get the location
//        getLastLocation();

        adapterTeam = new ArrayAdapter<>(this, R.layout.list_item, teamItem);
        mAutoCompletedTeam.setAdapter(adapterTeam);

        adapterState = new ArrayAdapter<>(this, R.layout.list_item, stateItem);
        mAutoCompletedState.setAdapter(adapterState);

        mUploadBtn.setOnClickListener(view ->{
            imageUpload();
        });



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

    private void handleSendImage(Intent intent2) {
        Uri imageUri =  intent2.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            try {
                Bitmap bitmap = getBitmapFromUri(imageUri);
                String tit;
                if (mTaskName!=null)
                {
                    tit = mTaskName.getText().toString();
                } else {
                    tit="task";
                }

                file = new File(getApplicationContext().getFilesDir(), tit+".jpg");
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
            case R.id.action_share:
                shareImage();
                return true;
            case R.id.action_profile:
                profile();
        }

        return true;
    }

    private void profile() {
        startActivity(new Intent(getApplicationContext(), Profile.class));
    }

    private void shareImage() {
        Intent intent2=getIntent();
        String imageAction= intent2.getAction();
        String imageType= intent2.getType();

        if (Intent.ACTION_SEND.equals(imageAction) && imageType != null)
        {
            if (imageType.equals("image/*"))
            {
                handleSendImage(intent2);
            }
        }
    }

    private void logout() {
        Amplify.Auth.signOut(
                ()-> {
                    startActivity(new Intent(AddTask.this, Login.class));
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

        public void imageUpload() {
        // Launches photo picker in single-select mode.
        // This means that the user can select one photo or video.
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(intent, REQUEST_CODE);
    }

    public void menuToSetting(){
        Intent newIntent  = new Intent(getApplicationContext(), Setting.class);
        startActivity(newIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            // Handler error
            Log.e(TAG, "onActivityResult: Error getting image from device");
        }
        switch (requestCode) {
            case REQUEST_CODE:
                // Get photo picker response for single select.
                Uri currentUri = data.getData();
                // Do stuff with the photo/video URI
                Log.i(TAG, "onActivityResult: the uri is => " + currentUri);

                try {
                    Bitmap bitmapImage = getBitmapFromUri(currentUri);

                    // Convert Bitmap to File
                    File file = new File(getApplicationContext().getFilesDir(), "image.jpg");
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.close();

                    // upload file to s3
                    imageKey = UUID.randomUUID().toString();
                    Amplify.Storage.uploadFile(
                            imageKey + ".jpg",
                            file,
                            result -> {
                                Log.i(TAG, "Successfully uploaded: " + result.getKey());

                                Bundle bundle = new Bundle();
                                bundle.putString("success", "success");

                                Message message = new Message();
                                message.setData(bundle);

                                handler.sendMessage(message);
                            },
                            storageFailure -> Log.e(TAG, "Failed Upload", storageFailure)

                    );
                    // Handler
                    handler = new Handler(Looper.getMainLooper(), msg -> {
                        Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.checkbox);
                        mUploadBtn.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                        mUploadBtn.setText("Upload is Done");
                        return true;
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return;
        }

    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        return image;
    }


    // For Give The Current Location
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
//    @SuppressLint("MissingPermission")
//    private void getLastLocation() {
//        // check if permissions are given
//        if (checkPermissions()) {
//
//            // check if location is enabled
//            if (isLocationEnabled()) {
//
//                // getting last
//                // location from
//                // FusedLocationClient
//                // object
//                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
//                        Location location = task.getResult();
//                        if (location == null) {
//                            requestNewLocationData();
//                        } else {
//                            latitude = location.getLatitude();
//                            longitude = location.getLongitude();
//
//                            LatLng coordinate = new LatLng(latitude, longitude);
//
//                        }
//                    }
//                });
//            } else {
//                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
//
//            }
//        } else {
//            // if permissions aren't available,
//            // request for permissions
//            requestPermissions();
//        }
//
//
//    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat
                        .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
//    private boolean isLocationEnabled() {
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//    }

    // If everything is alright then
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PERMISSION_ID) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
//            }
//        }
//    }


}