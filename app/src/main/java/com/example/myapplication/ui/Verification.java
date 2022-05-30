package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import com.example.myapplication.R;

public class Verification extends AppCompatActivity {

    EditText mCodeNumber;
    Button mVerifyButton;
    TextView mErrorText;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        // Inflate
        mCodeNumber = findViewById(R.id.verification_code);
        mVerifyButton = findViewById(R.id.verification_button);
        mErrorText = findViewById(R.id.verification_error_text);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        mVerifyButton.setOnClickListener(view ->{
            verifyCode(mCodeNumber.getText().toString(), email);
        });
    }

    private void verifyCode(String codeNumber, String email) {

        Amplify.Auth.confirmSignUp(
                email,
                codeNumber,
                result ->{

                    startActivity(new Intent(Verification.this, Login.class));
                    finish();

                    Bundle bundle = new Bundle();
                    bundle.putString("success", "success");

                    Message message = new Message();
                    message.setData(bundle);

                    handler.sendMessage(message);
                },
                error -> {

                    Bundle bundle = new Bundle();
                    bundle.putString("error", error.toString());

                    Message message = new Message();
                    message.setData(bundle);
                    handler.sendMessage(message);
                });

        handler = new Handler(Looper.getMainLooper(), msg -> {
            if (msg.getData().toString().equals("success"))
                Toast.makeText(this, "Your Email Is Verified", Toast.LENGTH_SHORT).show();
            else
                mErrorText.setVisibility(View.VISIBLE);
            return true;
        });
    }
}