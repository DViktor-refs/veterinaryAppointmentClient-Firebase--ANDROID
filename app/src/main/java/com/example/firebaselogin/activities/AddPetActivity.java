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
    private ArrayAdapter<String> speciesAdapter;
    private  NetworkChangeListener ncl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);
        initGui();
        loadSharedPreferences();

        tvAddAge.setOnClickListener(v -> {
            etAge.setText(""+addPlusOne());
        });

        tvDecAge.setOnClickListener(v -> {
            if (Integer.valueOf(etAge.getText().toString()) != 0) {
                etAge.setText(""+addMinusOne());
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

                if (!etPetname.getText().toString().isEmpty()) {
                    btnAddPet.setEnabled(true);
                }
                else {
                    btnAddPet.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnAddPet.setOnClickListener(v-> {
            saveSharedPreferences();
            Intent i = new Intent(AddPetActivity.this, RegisterActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
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

    private String addMinusOne() {
        int i = (Integer.valueOf(etAge.getText().toString()) - 1);
        return ""+i;
    }

    private String addPlusOne() {
        int i  = (Integer.valueOf(etAge.getText().toString()) + 1);
        return ""+i;
    }

    private void saveSharedPreferences() {
        SharedPreferences sharedPrefs = getSharedPreferences("petData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("petName", etPetname.getText().toString());
        editor.putString("petAge", etAge.getText().toString());
        editor.putInt("speciesSpinnerIndex", spinnerSpecies.getSelectedItemPosition());
        editor.putInt("isVaccinated", spinnerVaccinated.getSelectedItemPosition());
        editor.putInt("isNeutered", spinnerNeutered.getSelectedItemPosition());
        editor.commit();
    }

    private void loadSharedPreferences() {

        SharedPreferences petSharedPref = getSharedPreferences("petData", Context.MODE_PRIVATE);
        etPetname.setText(petSharedPref.getString("petName",""));
        etAge.setText(petSharedPref.getString("petAge","0"));

        spinnerSpecies.post(new Runnable() {
            @Override
            public void run() {
                spinnerSpecies.setSelection(petSharedPref.getInt("speciesSpinnerIndex",0));
            }
        });

        spinnerVaccinated.post(new Runnable() {
                    @Override
                    public void run() {
                        spinnerVaccinated.setSelection(petSharedPref.getInt("isVaccinated",0));
                    }
                });

        spinnerNeutered.post(new Runnable() {
            @Override
            public void run() {
                spinnerNeutered.setSelection(petSharedPref.getInt("isNeutered",0));
            }
        });
    }

    private void setAddPetButtonState() {
        if (!etPetname.getText().toString().isEmpty()) {
            btnAddPet.setEnabled(true);
        }
        else {
            btnAddPet.setEnabled(false);
        }
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
        speciesAdapter = new ArrayAdapter<>(this,
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