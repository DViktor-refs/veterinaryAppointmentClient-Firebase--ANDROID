package com.example.firebaselogin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.firebaselogin.R;
import com.example.firebaselogin.network.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView tvAddPet;
    private EditText etLastName, etFirstname, etEmail, etPassword;
    private Button btnRegister;
    private String petName;
    private String petSpecie;
    private String petAge;
    private String petIsVaccinated;
    private String petIsNeutered;
    private String user;
    private  String pass;
    private String lastName;
    private String firstName;
    private NetworkChangeListener ncl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initGui();

        tvAddPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, AddPetActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        btnRegister.setOnClickListener(v -> {
            Bundle bundle = getIntent().getExtras();
            register(bundle);

        });
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        ncl =  new NetworkChangeListener();
        registerReceiver(ncl,intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(ncl);
        super.onStop();
    }

    private void register(Bundle bundle) {

        if (areInputFieldsValid(etEmail, etPassword, etLastName, etFirstname, bundle)) {
            createUser(etEmail.getText().toString(), etPassword.getText().toString());
        }
    }

    private void createPet(Bundle bundle) {
        petName = bundle.getString("petName");
        petSpecie = bundle.getString("petSpecies");
        petAge = bundle.getString("petAge");
        petIsVaccinated = bundle.getString("isVaccianated");
        petIsNeutered = bundle.getString("isNeutered");
        Log.d("register datas: ", ""+petName + " - " +
                petSpecie + " - " +
                petAge + " - " +
                petIsNeutered + " - " +
                petIsVaccinated);

    }

    private void createUser(String user, String pass) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("------", "onComplete: " + "successful");
                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
                else {
                    Log.d("", "email : " + user);
                    Toast.makeText(RegisterActivity.this, "Something went wrong: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean areInputFieldsValid(EditText etEmail , EditText etPassword, EditText etLastName, EditText etFirstname, Bundle bundle) {
        user = etEmail.getText().toString();
        pass = etPassword.getText().toString();
        lastName = etLastName.getText().toString();
        firstName = etFirstname.getText().toString();

        boolean result=true;

        if (lastName.isEmpty()) {
            etLastName.requestFocus();
            etLastName.setError("Last name is required.");
            result = false;
        }

        if (firstName.isEmpty()) {
            etFirstname.requestFocus();
            etFirstname.setError("First name is required.");
            result = false;
        }

        if(user.isEmpty()) {
            etEmail.requestFocus();
            etEmail.setError("Email is required.");
            result = false;
        }

        if(!isEmailFormatValid(user)) {
            etEmail.requestFocus();
            etEmail.setError("Invalid email format.");
            result = false;
        }

        if(bundle == null) {
            tvAddPet.requestFocus();
            tvAddPet.setError("Please add a pet.");
            result = false;
        }
        return  result;
    }

    private boolean isEmailFormatValid(String user) {
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(user);
        return matcher.find();
    }

    private void initGui() {
        tvAddPet = findViewById(R.id.register_tv_addPet);
        btnRegister = findViewById(R.id.register_btn_startRegistration);
        etEmail = findViewById(R.id.register_et_email);
        etPassword = findViewById(R.id.register_et_password);
        etLastName = findViewById(R.id.register_et_lastName);
        etFirstname = findViewById(R.id.register_et_firstName);
        tvAddPet = findViewById(R.id.register_tv_addPet);
    }


}