package com.postaplus.etrack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class PickupVerificationActivity extends AppCompatActivity {
    private Spinner eventdate,eventcourier,eventstation;
    private String[] arraySpinner;
    private String[] arraySpinner1;
    Button clear, update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_verification);
        eventdate = (Spinner) findViewById(R.id.datespinner);
        eventcourier = (Spinner) findViewById(R.id.courierspinner);
        eventstation = (Spinner) findViewById(R.id.stationspinner);
        clear = (Button) findViewById(R.id.btnclear);
        update = (Button) findViewById(R.id.btnupdate);




        // code for station spinner
        this.arraySpinner = new String[]{
                "BAHRAIN", "DUBAI", "KUWAIT"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PickupVerificationActivity.this, android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventstation.setAdapter(adapter);

        // code for Courier spinner
        this.arraySpinner1 = new String[]{
                "abc", "efg", "hij", "klm", "nop", "qrs", "tuv", "wxy", "z", "zz",
        };

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(PickupVerificationActivity.this, android.R.layout.simple_spinner_item, arraySpinner1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventcourier.setAdapter(adapter1);

        // code for date spinner
        eventdate.setAdapter(new CalendarSpinnerAdapter(getBaseContext(),90));

        // code for clear button
        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_LONG).show();
            }

        });

        // code for Update button
        update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.image_click));
                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
            }

        });
    }

    }

