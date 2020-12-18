package com.smc.quicker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.smc.quicker.R;
import com.smc.quicker.service.FloatingService;
import com.smc.quicker.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private Spinner rowSpinner,colSpinner;
    private Switch floatSwitch,saveSwitch;
    private List<Integer> list = new ArrayList<>();
    private ArrayAdapter<Integer> adapter;
    private SharedPreferencesHelper helper;
    private boolean isAutoSave,isEdit;
    private int init;

    @Override
    protected void onStart() {
        super.onStart();
        isEdit = false;
        init = 0;
        rowSpinner = findViewById(R.id.row_spinner);
        colSpinner = findViewById(R.id.col_spinner);
        floatSwitch = findViewById(R.id.float_switch);
        saveSwitch = findViewById(R.id.save_switch);
        helper = new SharedPreferencesHelper(this);
        isAutoSave = helper.getAutoSave();
        int[] rowCol = helper.getRowCol();
        for(int i = 1;i<=5;i++)
            list.add(i);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rowSpinner.setAdapter(adapter);
        colSpinner.setAdapter(adapter);
        rowSpinner.setSelection(rowCol[0]-1);
        colSpinner.setSelection(rowCol[1]-1);
        rowSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(init>1 && isAutoSave)
                    save();
                else if(init>1)
                    isEdit = true;
                init++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        colSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(init>1 && isAutoSave)
                    save();
                else if(init>1)
                    isEdit = true;
                init++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        floatSwitch.setChecked(FloatingService.isRunService(this));
        saveSwitch.setChecked(isAutoSave);
        floatSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked && !FloatingService.isRunService(this))
                startService(new Intent(this, FloatingService.class));
            else if(!isChecked)
                stopService(new Intent(this, FloatingService.class));
        });
        saveSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isAutoSave = isChecked;
            helper.setAutoSave(isChecked);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public void onBackPressed() {
        if(!isAutoSave && isEdit)
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.alert_dark_frame)
                    .setTitle("注意")
                    .setMessage("是否保存设置?")
                    .setPositiveButton("是", (dialog, whichButton) -> {
                        save();
                        SettingsActivity.super.onBackPressed();
                    })
                    .setNegativeButton("否", (dialog, whichButton) -> {
                        SettingsActivity.super.onBackPressed();
                    }).create().show();
        else if(isAutoSave){
            save();
            super.onBackPressed();
        }
        else
            super.onBackPressed();
    }

    public void save(){
        int[] rowCol = new int[2];
        rowCol[0] = rowSpinner.getSelectedItemPosition()+1;
        rowCol[1] = colSpinner.getSelectedItemPosition()+1;
        helper.setRowCol(rowCol);
        FloatingService.updateSetting();
        isEdit = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //四个参数的含义:
        // 1.group的id;2.item的id;3.是否排序;4.将要显示的内容
        menu.add(0, 1, 0, "返回");
        menu.add(0, 2, 0, "保存设置");
        return true;
    }

    //菜单添加点击事件,需要重写onOptionsItemSelected（）方法。
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                finish();
                break;
            case 2:
                save();
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if(isAutoSave)
            save();
        super.onDestroy();
    }
}