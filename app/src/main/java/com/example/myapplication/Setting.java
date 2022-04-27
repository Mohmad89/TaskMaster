package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    public final String USERNAME = "username";
    private Button mBtnCreate;
    private EditText mEditUsername;

    private View.OnClickListener clickCreateUsername = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (mEditUsername.getText().toString().length() > 10){
                Toast.makeText(Setting.this, "Username must be less than 10 charecter", Toast.LENGTH_SHORT).show();
            } else {
                saveUsername();
            }


            // to back for last activity
            Setting.this.finish();

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mBtnCreate = findViewById(R.id.create_username);
        mEditUsername = findViewById(R.id.edit_username);

        mBtnCreate.setOnClickListener(clickCreateUsername);

        mEditUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!mBtnCreate.isEnabled()){
                    mBtnCreate.setEnabled(true);
                }

                if  (mEditUsername.toString().length() == 0){
                    mBtnCreate.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
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

    public void saveUsername () {
        String username = mEditUsername.getText().toString();

        // create SharedPreferences and set up an editor
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        // save the text
        preferenceEditor.putString("username", username);
        preferenceEditor.apply();

        Toast.makeText(this, "Username Has Been Created", Toast.LENGTH_SHORT).show();
    }




}