package com.example.firebaselogin.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.firebaselogin.network.NetworkChangeListener;
import com.example.firebaselogin.R;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button etBtnLogin;
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvResetPassword,tvRegister;
    private FirebaseAuth mAuth;
    private NetworkChangeListener networkChangeListener;

    //<editor-fold desc="Activity Lifecycle">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initFirebase();
        initNetworkStateListener();
        initGui();
        clearSharedPrefs();
        loadSharedPrefs();
        initListeners();
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        saveSharedPrefs();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        showQuitAlertDialog();
    }

    private void initListeners() {
        etBtnLogin.setOnClickListener(v -> login());
        tvResetPassword.setOnClickListener(v -> resetPassword());
        tvRegister.setOnClickListener(v -> register());
    }

    private void initNetworkStateListener() {
        networkChangeListener = new NetworkChangeListener();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
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
            mAuth.signInWithEmailAndPassword(etUsername.getText().toString(),etPassword.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    startUserActivity();
                }
                else {
                    Toast.makeText(MainActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
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
        SharedPreferences petdataSharedPrefs = getSharedPreferences("petData", Context.MODE_PRIVATE);
        SharedPreferences ownerSharedPrefs = getSharedPreferences("ownerData", Context.MODE_PRIVATE);
        petdataSharedPrefs.edit().clear().apply();
        ownerSharedPrefs.edit().clear().apply();
    }

    private void showQuitAlertDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setTitle("Quit")
                .setMessage("Are you sure?")
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    MainActivity.super.onBackPressed();
                    finish();
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void saveSharedPrefs() {
        SharedPreferences sharedPrefs = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("lastEmailAddress", etUsername.getText().toString());
        editor.apply();
    }

    private void loadSharedPrefs() {
        SharedPreferences sharedPrefs = getSharedPreferences("ownerData", Context.MODE_PRIVATE);
        etUsername.setText(sharedPrefs.getString("lastEmailAddress",""));
    }

    private void initGui() {
        tvResetPassword = findViewById(R.id.login_tv_forgotpassword);
        tvRegister = findViewById(R.id.login_tv_notregistered);
        etBtnLogin = findViewById(R.id.reset_btn_reset);
        etUsername = findViewById(R.id.register_et_flastName);
        etPassword = findViewById(R.id.addpet_spinner_petspecie);
    }

}