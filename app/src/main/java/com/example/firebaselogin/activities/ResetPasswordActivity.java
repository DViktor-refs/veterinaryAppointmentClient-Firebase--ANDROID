package com.example.firebaselogin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.firebaselogin.R;
import com.example.firebaselogin.network.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnReset, btnCancel;
    private EditText etEmail;
    private TextView tvStatus;
    private FirebaseAuth mAuth;
    private NetworkChangeListener ncl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initGui();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        ncl = new NetworkChangeListener();
        registerReceiver(ncl,intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(ncl);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.reset_btn_back :
                startLoginActivity();
                break;
            case R.id.reset_btn_reset:
                resetPassword();
                break;
        }
    }

    private void resetPassword() {
        String email = etEmail.getText().toString();
        if (!isEmailFormatValid(email)) {
            tvStatus.setText("Invalid email address.");
            etEmail.requestFocus();
        }
        else {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        tvStatus.setText("Your password successfully resetted. \nPlease check your email. ");
                        Intent i = new Intent(ResetPasswordActivity.this, MainActivity.class);
                    }
                    else {
                        tvStatus.setText("Error: " + task.getException().getMessage());
                    }
                }
            });
        }
    }

    private boolean isEmailFormatValid(String user) {
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(user);
        return matcher.find();
    }

    private void initGui() {
        btnReset = findViewById(R.id.reset_btn_reset);
        btnCancel = findViewById(R.id.reset_btn_back);
        etEmail = findViewById(R.id.register_et_flastName);
        tvStatus = findViewById(R.id.reset_tv_status);
    }

    private void startLoginActivity() {
        Intent i = new Intent(ResetPasswordActivity.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
