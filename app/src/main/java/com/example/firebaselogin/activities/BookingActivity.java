package com.example.firebaselogin.activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.firebaselogin.R;
import com.example.firebaselogin.models.Reservation;
import com.example.firebaselogin.network.NetworkChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private DatabaseReference dbRef;
    private FirebaseUser user;
    private  SimpleDateFormat sdf;
    private  CalendarView calendarView;
    private  Spinner spinner;
    private  Button btnReservation;
    private  TextView tvSelectedDate;
    private  NetworkChangeListener ncl;
    private  final String[] reservableTimeIntervalsArr = {"09:00-09:15","10:00-10:15","11:00-11:15","13:00-13:15","14:00-14:15","15:00-15:15"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLocationToHu();
        setContentView(R.layout.activity_booking);
        initGui();
        initDb();
        initCalendar();
        setCurrentDateOnGui();
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSpinner();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        ncl = new NetworkChangeListener();
        registerReceiver(ncl,intentFilter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(ncl);
        super.onStop();
    }

    private void initListeners() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            tvSelectedDate.setText(getConvertedDateFormat(year, month, dayOfMonth));
            updateSpinner();
        });

        btnReservation.setOnClickListener(v -> sendReservationToDb(
                spinner.getSelectedItem().toString(),
                tvSelectedDate.getText().toString(),
                user.getEmail()
        ));
    }

    private void sendReservationToDb(String selectedItemInSpinner, String date, String userEmail) {
        String path = date.replaceAll("/","") + "/" + selectedItemInSpinner;
        Log.d(TAG, "sendReservationToDb: date = " + path);
        dbRef.child(path).setValue(new Reservation(userEmail));
        updateSpinner();
    }

    private void updateSpinner() {
        btnReservation.setEnabled(false);
        String selectedDate = tvSelectedDate.getText().toString();
        selectedDate = selectedDate.replaceAll("/","");
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(selectedDate);

        dbRef.get().addOnCompleteListener(task -> {
            List<String> templist = new ArrayList<>(Arrays.asList(reservableTimeIntervalsArr));
            for (DataSnapshot snapshot : task.getResult().getChildren()) {
                for (int i = 0; i < templist.size(); i++) {
                    if (templist.get(i).equals(snapshot.getKey())) {
                        templist.remove(i);
                    }
                }
            }
            setSpinnerItems(templist);
            btnReservation.setEnabled(templist.size() != 0);

        }).addOnFailureListener(e -> btnReservation.setEnabled(false));
    }

    private void setSpinnerItems(List<String> templist) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, templist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private String getConvertedDateFormat(int year, int month, int dayOfMonth) {
        String result = year + "/";
        if ((month+1) < 10) {                   // onSelectedDayChange adds wrong month value.
            result += "0"+ (month+1) + "/";
        }
        else {
            result += (month+1) + "/";
        }

        if (dayOfMonth<10) {
            result += "0" + dayOfMonth;
        }
        else {
            result += dayOfMonth;
        }

        return result;
    }

    private void setCurrentDateOnGui() {
        tvSelectedDate.setText(getCurrentDate());
    }

    private String getCurrentDate() {
        String selectedDate = sdf.format(new Date(calendarView.getDate()));
        selectedDate = "20"+selectedDate;
        String yearSub = selectedDate.substring(0,4);
        String monthSub = selectedDate.substring(4,6);
        String daySub = selectedDate.substring(6,8);
        selectedDate = yearSub + "/" + monthSub +"/" + daySub;
        Log.d(TAG, "getCurrentData: "+selectedDate);
        return selectedDate;
    }

    private void initDb() {
        FirebaseDatabase rootDb = FirebaseDatabase.getInstance();
        dbRef = rootDb.getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    private void initCalendar() {
        Calendar c = Calendar.getInstance();
        calendarView.setMinDate(c.getTimeInMillis());
        sdf = new SimpleDateFormat("yyMMdd");
        updateSpinner();
    }

    private void initGui() {
        calendarView = findViewById(R.id.booking_calendarview);
        tvSelectedDate = findViewById(R.id.booking_tv_selected_date);
        spinner = findViewById(R.id.booking_spinner);
        btnReservation = findViewById(R.id.booking_btn_reserve);
        btnReservation.setEnabled(false);
    }

    @SuppressWarnings("deprecation")
    private void changeLocationToHu() {
        String languageToLoad  = "hu";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
}