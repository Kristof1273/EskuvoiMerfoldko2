package com.example.eskuvoihelyszinlefoglaloapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final int SECRET_KEY = 99;
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private SharedPreferences preferences;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Bundle bundle = getIntent().getExtras();
        int secret_key = bundle.getInt("SECRET_KEY");
        if(secret_key!=99){
            finish();
        }
        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
    }
    public void navigate_to_venues(View view) {
        Intent intent = new Intent(this, VenueActivity.class);
        startActivity(intent);
    }
    public void register(View view) {
        EditText usernameEditText = findViewById(R.id.edtusername);
        EditText emailEditText = findViewById(R.id.edtEmail);
        EditText passwordEditText = findViewById(R.id.edtPassword);


        String usernameStr = usernameEditText.getText().toString();
        String emailStr = emailEditText.getText().toString();
        String passwordStr = passwordEditText.getText().toString();

        Log.d(MainActivity.class.getName(), "Username: " + usernameStr +", Email: " + emailStr + ", Password: " + passwordStr);
        mAuth.createUserWithEmailAndPassword(emailStr,passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG,"User created successfully");
                    navigate_to_login(view);
                }else{
                Log.d(LOG_TAG,"User creation failed");
                Toast.makeText(RegisterActivity.this,"User creation failed"+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void navigate_to_login(View view) {
        Intent intent = new Intent(this, loginActivity.class);
        intent.putExtra("SECRET_KEY",SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();

        editor.apply();
    }



}
