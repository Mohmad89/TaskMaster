package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.services.storage.internal.InternalTestStorage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.example.myapplication.R;

public class SignUp extends AppCompatActivity {

    private static final String TAG = SignUp.class.getSimpleName();

    private EditText mEmail, mPassword;
    private Button mSignUpButton;
    private TextView mLoginPage, mTextError, mUsername;
    private ProgressBar mProgressBar;

    private Handler handler ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Inflate
        mEmail = findViewById(R.id.sign_email_text);
        mPassword = findViewById(R.id.sign_password_text);
        mSignUpButton = findViewById(R.id.sign_button);
        mLoginPage = findViewById(R.id.login_page);
        mProgressBar = findViewById(R.id.sign_progress_bar);
        mTextError = findViewById(R.id.sign_error_text);
        mUsername = findViewById(R.id.sign_username_text);

        mLoginPage.setOnClickListener(view -> {
            Intent navigateToLoginPage = new Intent(this, Login.class);
            startActivity(navigateToLoginPage);
        });


//        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEND) {
//                    mSignUpButton.setEnabled(true);
//                }
//                return false;
//            }
//        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);

                signUp(mEmail.getText().toString(),
                        mPassword.getText().toString(),
                        mUsername.getText().toString());
            }
        });
    }

    private void signUp(String email, String password, String username) {

        // add as many attributes as you wish
        AuthSignUpOptions options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), email)
                .userAttribute(AuthUserAttributeKey.nickname(), username)
                .build();

        Amplify.Auth.signUp(email, password, options,
                result -> {

                    Log.i(TAG, "Result: " + result.toString());
                    mProgressBar.setVisibility(View.INVISIBLE);

                    Intent intent = new Intent(SignUp.this, Verification.class);
                    intent.putExtra("email", email);
                    startActivity(intent);

                    finish();
                },
                error -> {
                    Log.e(TAG, "Sign up failed", error);

                    mProgressBar.setVisibility(View.INVISIBLE);
                    Bundle bundle = new Bundle();
                    bundle.putString("Error", error.toString());

                    Message message = new Message();
                    message.setData(bundle);

                    handler.sendMessage(message);
                }
        );

        handler = new Handler(Looper.getMainLooper(), msg -> {
            String text = msg.getData().toString();
            if (text.contains("Password did not conform"))
                mTextError.setText("Password not long enough ! ");
            else
                mTextError.setText("Email is already exists ");
            mTextError.setVisibility(View.VISIBLE);
            return true;
        });
    }
}