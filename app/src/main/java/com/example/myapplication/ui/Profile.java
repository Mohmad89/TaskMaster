package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.example.myapplication.R;

public class Profile extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    TextView mEmailProfile, mUsernameProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mEmailProfile = findViewById(R.id.email_profile_text);
        mUsernameProfile = findViewById(R.id.username_profile_text);

        Amplify.Auth.fetchUserAttributes(
                attributes ->{
                    Log.i(TAG, "onCreate: " + attributes);
                },
                error -> Log.e("AuthDemo", "Failed to fetch user attributes.", error)
        );
    }
}