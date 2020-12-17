package com.smc.quicker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.smc.quicker.R;
import com.smc.quicker.service.FloatingService;
import com.smc.quicker.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private Spinner rowSpinner,colSpinner;
    private List<Integer> list = new ArrayList<>();
    private ArrayAdapter<Integer> adapter;
    private SharedPreferencesHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        rowSpinner = findViewById(R.id.row_spinner);
        colSpinner = findViewById(R.id.col_spinner);
        helper = new SharedPreferencesHelper(this);
        int[] rowCol = helper.getRowCol();
        for(int i = 1;i<=5;i++)
            list.add(i);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rowSpinner.setAdapter(adapter);
        colSpinner.setAdapter(adapter);
        rowSpinner.setSelection(rowCol[0]-1);
        colSpinner.setSelection(rowCol[1]-1);
    }

    @Override
    public void onBackPressed() {
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
    }

    public void save(){
        int[] rowCol = new int[2];
        rowCol[0] = rowSpinner.getSelectedItemPosition()+1;
        rowCol[1] = colSpinner.getSelectedItemPosition()+1;
        helper.setRowCol(rowCol);
        FloatingService.updateSetting();
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
}