package com.smc.quicker.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.smc.quicker.adapter.PackageListAdapter;
import com.smc.quicker.R;
import com.smc.quicker.entity.AppInfo;
import com.smc.quicker.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class PackageListActivity extends AppCompatActivity {

    private List<AppInfo> appList;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private ListView listView;
    private PackageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list);
        List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
        appList = new ArrayList<>();
        for(PackageInfo packageInfo : packageInfos){
            if(getPackageManager().getLaunchIntentForPackage(packageInfo.packageName)!=null)
                appList.add(new AppInfo(packageInfo.packageName,
                        packageInfo.applicationInfo.loadLabel(getPackageManager()).toString(),packageInfo.applicationInfo.uid,0,0));
        }
        adapter = new PackageListAdapter(PackageListActivity.this, R.layout.app_item, appList,getPackageManager());
        listView = findViewById(R.id.mListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            AppInfo appInfo = appList.get(position);
            Intent intent = new Intent(PackageListActivity.this,MainActivity.class);
            intent.putExtra("appinfo", appInfo);
            setResult(RESULT_OK,intent);
            finish();
        });
        String[] appNames = new String[appList.size()];
        int i = 0;
        for(AppInfo appInfo:appList){
            appNames[i++] = appInfo.getAppName();
        }
        AutoCompleteTextView myAutoCompleteTextView = findViewById(R.id.search_TextView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,appNames);
        myAutoCompleteTextView.setAdapter(arrayAdapter);   //设置适配器
        myAutoCompleteTextView.setThreshold(1);   //定义需要用户输入的字符数
        dbHelper = new DBHelper(PackageListActivity.this, "appinfo.db", null, 3);//创建帮助器对象
        myAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getSearchPackage(myAutoCompleteTextView.getText().toString());
            }
        });
    }

    public void getSearchPackage(String searchPackageName){
        database = dbHelper.getWritableDatabase();
        Cursor cursor = dbHelper.onQuery(database,searchPackageName);
        appList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                AppInfo appInfo1 = new AppInfo();
                appInfo1.setUid(Integer.parseInt(cursor.getString(cursor.getColumnIndex(AppInfo.KEY_uid))));
                appInfo1.setAppName(cursor.getString(cursor.getColumnIndex(AppInfo.KEY_appName)));
                appInfo1.setPackageName(cursor.getString(cursor.getColumnIndex(AppInfo.KEY_packageName)));
                appInfo1.setTimes(0);
                appInfo1.setAppOrder(0);
                appList.add(appInfo1);
            } while (cursor.moveToNext());
        }
        database.close();
        adapter = new PackageListAdapter(PackageListActivity.this, R.layout.app_item, appList,getPackageManager());
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //四个参数的含义:
        // 1.group的id;2.item的id;3.是否排序;4.将要显示的内容
        menu.add(0, 1, 0, "返回");
        return true;
    }

    //菜单添加点击事件,需要重写onOptionsItemSelected（）方法。
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(PackageListActivity.this,MainActivity.class);
                setResult(RESULT_CANCELED,intent);
                finish();
                break;
        }
        return true;
    }
}
