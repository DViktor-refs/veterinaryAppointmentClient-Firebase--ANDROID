package com.example.firebaselogin.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.firebaselogin.R;
import com.example.firebaselogin.network.NetworkChangeListener;

public class AddPetActivity extends AppCompatActivity {

    private Spinner spinnerSpecies, spinnerVaccinated, spinnerNeutered;
    private TextView tvAddAge, tvDecAge;
    private EditText etAge,etPetname;
    private Button btnAddPet;
    private NetworkChangeListener ncl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);
        initGui();
        loadSharedPreferences();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAddPetButtonState();
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
        saveSharedPreferences();
    }

    private void initListeners() {
        tvAddAge.setOnClickListener(v -> etAge.setText(addPlusOne()));

        tvDecAge.setOnClickListener(v -> {
            if (Integer.parseInt(etAge.getText().toString()) != 0) {
                etAge.setText(addMinusOne());
            }
        });

        etPetname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!etPetname.getText().toString().isEmpty()) {
                    btnAddPet.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnAddPet.setEnabled(!etPetname.getText().toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnAddPet.setOnClickListener(v-> {
            saveSharedPreferences();
            Intent i = new Intent(AddPetActivity.this, RegisterActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private String addMinusOne() {
        int i = (Integer.parseInt(etAge.getText().toString()) - 1);
        return String.valueOf(i);
    }

    private String addPlusOne() {
        int i  = (Integer.parseInt(etAge.getText().toString()) + 1);
        return String.valueOf(i);
    }

    private void saveSharedPreferences() {
        SharedPreferences sharedPrefs = getSharedPreferences("petData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("petName", etPetname.getText().toString());
        editor.putString("petAge", etAge.getText().toString());
        editor.putInt("speciesSpinnerIndex", spinnerSpecies.getSelectedItemPosition());
        editor.putString("speciesSpinnerValue", spinnerSpecies.getSelectedItem().toString());
        editor.putInt("isVaccinated", spinnerVaccinated.getSelectedItemPosition());
        editor.putString("isVaccinatedValue", spinnerVaccinated.getSelectedItem().toString());
        editor.putInt("isNeutered", spinnerNeutered.getSelectedItemPosition());
        editor.putString("isNeuteredValue", spinnerNeutered.getSelectedItem().toString());
        editor.apply();
    }

    private void loadSharedPreferences() {
        SharedPreferences petSharedPref = getSharedPreferences("petData", Context.MODE_PRIVATE);
        etPetname.setText(petSharedPref.getString("petName",""));
        etAge.setText(petSharedPref.getString("petAge","0"));
        restoreSpinnerValues(petSharedPref);
    }

    private void restoreSpinnerValues(SharedPreferences petSharedPref) {
        spinnerSpecies.post(() -> spinnerSpecies.setSelection(petSharedPref.getInt("speciesSpinnerIndex",0)));
        spinnerVaccinated.post(() -> spinnerVaccinated.setSelection(petSharedPref.getInt("isVaccinated",0)));
        spinnerNeutered.post(() -> spinnerNeutered.setSelection(petSharedPref.getInt("isNeutered",0)));
    }

    private void setAddPetButtonState() {
        btnAddPet.setEnabled(!etPetname.getText().toString().isEmpty());
    }

    private void setSpinnerItems() {
        setSpeciesSpinnerAdapter();
        setVaccinatedSpinnerAdapter();
        setNeuteredSpinnerAdapter();
    }

    private void setNeuteredSpinnerAdapter() {
        ArrayAdapter<String> neuteredAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.isNeutered));
        neuteredAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNeutered.setAdapter(neuteredAdapter);
    }

    private void setVaccinatedSpinnerAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.isVaccinated));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVaccinated.setAdapter(adapter);
    }

    private void setSpeciesSpinnerAdapter() {
        ArrayAdapter<String> speciesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.petSpecies));
        speciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecies.setAdapter(speciesAdapter);
    }

    private void initGui() {
        spinnerSpecies = findViewById(R.id.addpet_spinner_petspecie);
        spinnerVaccinated = findViewById(R.id.addpet_spinner_petIsVaccinated);
        spinnerNeutered = findViewById(R.id.addpet_spinner_isNeutered);
        tvAddAge = findViewById(R.id.addpet_tv_add);
        tvDecAge = findViewById(R.id.addPet_tv_dec);
        etAge = findViewById(R.id.addpet_et_age);
        etPetname = findViewById(R.id.addpet_et_petname);
        btnAddPet = findViewById(R.id.addpet_btn_add);
        setSpinnerItems();
    }

}