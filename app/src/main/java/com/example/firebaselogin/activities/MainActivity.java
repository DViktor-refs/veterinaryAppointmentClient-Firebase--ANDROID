package com.example.firebaselogin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.firebaselogin.network.NetworkChangeListener;
import com.example.firebaselogin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button etBtnLogin;
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvResetPassword,tvRegister;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private NetworkChangeListener networkChangeListener;

    //<editor-fold desc="Activity Lifecycle">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initFirebase();
        initNetworkStateListener();
        initGui();

        etBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        tvResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,intentFilter);
        firebaseUser = mAuth.getCurrentUser();
        clearSharedPrefs();
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        showQuitAlertDialog();
    }

    private void initNetworkStateListener() {
        networkChangeListener = new NetworkChangeListener();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
    }

    private void resetPassword() {
        Intent i = new Intent(MainActivity.this, ResetPasswordActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void register() {
        Intent i = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    //</editor-fold>
    private void login() {
        if(areInputFieldsValid(etUsername, etPassword)) {
            mAuth.signInWithEmailAndPassword(etUsername.getText().toString(),etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startUserActivity();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void startUserActivity() {
        Intent i = new Intent(MainActivity.this, UserActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private boolean areInputFieldsValid(EditText etUsername, EditText etPassword) {
        String user = etUsername.getText().toString();
        String pass = etPassword.getText().toString();
        boolean result=true;

        if(user.isEmpty()) {
            etUsername.requestFocus();
            etUsername.setError("Email is required.");
            result = false;
        }

        else if(!isEmailFormatValid(user)) {
            etUsername.requestFocus();
            etUsername.setError("Invalid email format.");
            result = false;
        }

        else if(pass.isEmpty()) {
            etPassword.requestFocus();
            etPassword.setError("Password is required.");
            result = false;
        }
        return  result;
    }

    private boolean isEmailFormatValid(String user) {
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(user);
        return matcher.find();
    }

    private void clearSharedPrefs() {
        SharedPreferences sharedPrefs = getSharedPreferences("petData", Context.MODE_PRIVATE);
        sharedPrefs.edit().clear().commit();
    }

    private void showQuitAlertDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setTitle("Quit")
                .setMessage("Are you sure?")
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.super.onBackPressed();
                        finish();
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void initGui() {
        tvResetPassword = findViewById(R.id.login_tv_forgotpassword);
        tvRegister = findViewById(R.id.login_tv_notregistered);
        etBtnLogin = findViewById(R.id.reset_btn_reset);
        etUsername = findViewById(R.id.register_et_flastName);
        etPassword = findViewById(R.id.addpet_spinner_petspecie);
    }

}