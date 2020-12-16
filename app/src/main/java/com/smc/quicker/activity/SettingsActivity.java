package com.smc.quicker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.smc.quicker.R;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private Spinner rowSpinner,colSpinner;
    private List<Integer> list = new ArrayList<>();
    private ArrayAdapter<Integer> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        rowSpinner = findViewById(R.id.row_spinner);
        colSpinner = findViewById(R.id.col_spinner);
        for(int i = 1;i<=5;i++)
            list.add(i);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rowSpinner.setAdapter(adapter);
        colSpinner.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}