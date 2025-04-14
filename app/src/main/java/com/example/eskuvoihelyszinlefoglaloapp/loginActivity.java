package com.example.eskuvoihelyszinlefoglaloapp;

import android.app.job.JobScheduler;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class loginActivity extends AppCompatActivity {
    private static final int SECRET_KEY = 99;
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private FirebaseAuth mAuth;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private static final int RC_SIGN_IN = 9001;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bundle bundle = getIntent().getExtras();
        int secret_key = bundle.getInt("SECRET_KEY");
        if(secret_key!=99){
            finish();
        }
        mAuth = FirebaseAuth.getInstance();
    }

    public void navigate_to_register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY",SECRET_KEY);
        startActivity(intent);
    }

    public void navigate_to_venues(View view) {
        Intent intent = new Intent(this, VenueActivity.class);
        startActivity(intent);
    }
    public void login(View view) {
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPassword = findViewById(R.id.edtPassword);

        String emailStr = edtEmail.getText().toString();
        String passwordStr = edtPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Login successful");
                    navigate_to_venues(view);
                } else {
                    Log.d(LOG_TAG, "Login unsuccessful");
                    Toast.makeText(loginActivity.this, "Login unsuccessful" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void guest_login(View view) {
    mAuth.signInAnonymously().addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Log.d(LOG_TAG, "Anonim login successful");
                navigate_to_venues(view);
            } else {
                Log.d(LOG_TAG, "Anonim login unsuccessful");
                Toast.makeText(loginActivity.this, "Login unsuccessful" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    });
    }

    public void google_login(View view) {
    }

}
