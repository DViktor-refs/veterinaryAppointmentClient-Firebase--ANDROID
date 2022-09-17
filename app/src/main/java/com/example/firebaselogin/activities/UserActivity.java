package com.example.firebaselogin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.example.firebaselogin.R;
import com.example.firebaselogin.network.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference ownerRef;
    private TextView tvName, tvPetName, tvPetAge, tvPetSpecies, tvPetVaccinated, tvPetNeutered;
    private Button btnBooking;
    private NetworkChangeListener ncl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usermenu);
        initalize();
        setFields(user.getEmail());
        btnBooking.setOnClickListener(v -> {
            Intent i = new Intent(UserActivity.this, BookingActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(UserActivity.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void setFields(String email) {
        ownerRef.whereEqualTo("ownerEmail", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        tvName.setText(snapshot.getString("ownerName"));
                        tvPetName.setText(snapshot.getString("petName"));
                        tvPetAge.setText(""+snapshot.getString("petAge"));
                        tvPetSpecies.setText(snapshot.getString("speciesSpinnerValue"));
                        tvPetVaccinated.setText(""+snapshot.getString("isVaccinatedValue"));
                        tvPetNeutered.setText(""+snapshot.getString("isNeutaredValue"));
                    }
                }
            }
        });
    }

    private void initalize() {
        initFirebase();
        initGui();
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        ownerRef = db.collection("owner");
    }

    private void initGui() {
        tvName = findViewById(R.id.booking_tv_username);
        tvPetName = findViewById(R.id.usermenu_tv_petName);
        tvPetAge = findViewById(R.id.usermenu_tv_age);
        tvPetSpecies = findViewById(R.id.usermenu_tv_species);
        tvPetVaccinated = findViewById(R.id.usermenu_tv_isVaccinated);
        tvPetNeutered = findViewById(R.id.usermenu_tv_isNeutered);
        btnBooking = findViewById(R.id.usermenu_btn_booking);
    }

}