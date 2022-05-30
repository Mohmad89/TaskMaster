package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.example.myapplication.R;

public class Login extends AppCompatActivity {

    private static final String TAG = Login.class.getSimpleName();

    private EditText mEmail, mPassword;
    private Button mLoginButton;
    private TextView mSignUpPage;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inflate
        mEmail = findViewById(R.id.login_email_text);
        mPassword = findViewById(R.id.login_password_text);
        mLoginButton = findViewById(R.id.login_button);
        mSignUpPage = findViewById(R.id.sign_up_page);
        mProgressBar = findViewById(R.id.progress_bar_login);

        // Initialize Amplify
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException error) {
        }


        mSignUpPage.setOnClickListener(view ->{
            Intent navigateToLoginPage = new Intent(this, SignUp.class);
            startActivity(navigateToLoginPage);
        });

        mLoginButton.setOnClickListener(view -> {
        mProgressBar.setVisibility(View.VISIBLE);
        loginAut(mEmail.getText().toString(), mPassword.getText().toString());
        });
        authSession();

    }

    private void loginAut(String email, String password) {
        Amplify.Auth.signIn(
                email,
                password,
                result ->{
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(Login.this, MainActivity.class);
//                    intent.putExtra("username",result )
                    startActivity(intent);
                    finish();
                },
                error -> {}
        );
    }


    private void authSession() {
        Amplify.Auth.fetchAuthSession(
                result -> {
                    Log.i(TAG, "Auth Session ");
                },
                error -> Log.e(TAG, "Auth Session Error ", error)
        );
    }
}