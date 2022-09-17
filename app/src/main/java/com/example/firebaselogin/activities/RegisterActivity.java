package com.example.firebaselogin.activities;

import static android.content.ContentValues.TAG;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
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
    private String lastName;
    private String firstName;
    private String pass;
    private NetworkChangeListener ncl;
    private FirebaseFirestore db;
    private CollectionReference ownerRef;
    private FirebaseUser fireBaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initGui();
        initFirebase();
        loadSharedPrefs();

        tvAddPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSharedPrefs();
                Intent i = new Intent(RegisterActivity.this, AddPetActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        btnRegister.setOnClickListener(v -> {

            Bundle bundle = createBundle();
            register(bundle);

        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        ncl =  new NetworkChangeListener();
        registerReceiver(ncl,intentFilter);
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveSharedPrefs();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(ncl);
        super.onStop();
    }

    private void register(Bundle bundle) {

        if (areInputFieldsValid(etEmail, etPassword, etLastName, etFirstname, bundle)) {
            createUser(etEmail.getText().toString(), etPassword.getText().toString());
            createPet(bundle);
        }
    }

    private Bundle createBundle() {
        Bundle bundle = new Bundle();
        SharedPreferences petSharedPref = getSharedPreferences("petData", Context.MODE_PRIVATE);
        bundle.putString("petName", petSharedPref.getString("petName", ""));
        bundle.putString("petAge", petSharedPref.getString("petAge", ""));
        bundle.putString("speciesSpinnerValue", petSharedPref.getString("speciesSpinnerValue", ""));
        bundle.putString("isVaccinatedValue", petSharedPref.getString("isVaccinatedValue", ""));
        bundle.putString("isNeuteredValue", petSharedPref.getString("isNeuteredValue", ""));
        return bundle;
    }

    private void saveSharedPrefs() {
        SharedPreferences sharedPrefs = getSharedPreferences("ownerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("secondName", etLastName.getText().toString());
        editor.putString("firstName", etFirstname.getText().toString());
        editor.putString("email", etEmail.getText().toString());
        editor.putString("password", etPassword.getText().toString());
        editor.apply();
        Log.d(TAG, "firstname : " + firstName);
        Log.d(TAG, "SHAREDPREFS SAVED");
    }

    private void loadSharedPrefs() {
        SharedPreferences sharedPrefs = getSharedPreferences("ownerData", Context.MODE_PRIVATE);
        etFirstname.setText(sharedPrefs.getString("firstName",""));
        Log.d(TAG, "firstname : " + firstName);
        etLastName.setText(sharedPrefs.getString("secondName",""));
        etEmail.setText(sharedPrefs.getString("email",""));
        etPassword.setText(sharedPrefs.getString("password",""));
        Log.d(TAG, "SHAREDPREFS LOADED");
    }

    private void createPet(Bundle bundle) {
        if (mAuth.getCurrentUser() != null && bundle != null) {
            Map<String, String> user = getUserMap(bundle);

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

            ownerRef.document().set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else {
            sendErrorMessage();
        }

    }

    private Map<String, String> getUserMap(Bundle bundle) {
        Map<String, String> result = new HashMap<>();
        result.put("ownerName", etFirstname.getText().toString());
        result.put("ownerEmail", etEmail.getText().toString());
        result.put("petName", bundle.getString("petName"));
        result.put("petAge", bundle.getString("petAge"));
        result.put("speciesSpinnerValue", bundle.getString("speciesSpinnerValue"));
        result.put("isVaccinatedValue", bundle.getString("isVaccinatedValue"));
        result.put("isNeuteredValue", bundle.getString("isNeuteredValue"));
        return result;
    }

    private void sendErrorMessage() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RegisterActivity.this);
        alertBuilder.setTitle("Error")
                .setMessage("Your session has expired. Please sign in again.")
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RegisterActivity.super.onBackPressed();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void createUser(String user, String pass) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        boolean result;

        if (lastName.isEmpty()) {
            etLastName.setError("Last name is required.");
        }

        if (firstName.isEmpty()) {
            etFirstname.setError("First name is required.");
        }

        if(user.isEmpty()) {
            etEmail.setError("Email is required.");
        }

        if(pass.isEmpty()) {
            etPassword.setError("Password is required.");
        }

        if(!isEmailFormatValid(user)) {
            etEmail.setError("Invalid email format.");
        }

        if(bundle == null) {
            tvAddPet.setError("Please add a pet.");
        }

        if(!isBundleValid(bundle)) {
            tvAddPet.setError("Please add a pet.");
            result = false;
        }
        else {
            result = true;
        }

        return result;
    }

    private boolean isBundleValid(Bundle bundle) {
        boolean result = false;
        if   (  bundle.getString("petName") != "" ||
                bundle.getString("petAge") != "" ||
                bundle.getString("speciesSpinnerValue") != "" ||
                bundle.getString("isVaccinatedValue") != "" ||
                bundle.getString("isNeuteredValue") != "" ) {
            result = true;
        }
        Log.d(TAG, "isBundleValid: " + result);
        return result;

    }

    private boolean isEmailFormatValid(String user) {
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(user);
        return matcher.find();
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fireBaseUser = mAuth.getCurrentUser();
        ownerRef = db.collection("owner");
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