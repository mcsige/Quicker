package com.smc.quicker.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.smc.quicker.adapter.PackageListAdapter;
import com.smc.quicker.R;
import com.smc.quicker.entity.AppInfo;
import com.smc.quicker.service.FloatingService;
import com.smc.quicker.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class PackageListActivity extends AppCompatActivity {

    private List<AppInfo> appList;
    private ListView listView;
    private PackageListAdapter adapter;
    private String[] appNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list);
        listView = findViewById(R.id.mListView);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            AppInfo appInfo = appList.get(position);
            Intent intent = new Intent(PackageListActivity.this,MainActivity.class);
            intent.putExtra("appinfo", appInfo);
            setResult(RESULT_OK,intent);
            finish();
        });
        initView();
        appNames = new String[appList.size()];
        int i = 0;
        for(AppInfo appInfo:appList){
            appNames[i++] = appInfo.getAppName();
        }
        AutoCompleteTextView myAutoCompleteTextView = findViewById(R.id.search_TextView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,appNames);
        myAutoCompleteTextView.setAdapter(arrayAdapter);   //设置适配器
        myAutoCompleteTextView.setThreshold(1);   //定义需要用户输入的字符数
        myAutoCompleteTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        myAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            getSearchPackage(myAutoCompleteTextView.getText().toString());
        });
    }

    @Override
    protected void onStart() {
        initView();
        super.onStart();
    }

    private void initView(){
        if(FloatingService.packageList==null) {
            List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
            appList = new ArrayList<>();
            for (PackageInfo packageInfo : packageInfos) {
                if (getPackageManager().getLaunchIntentForPackage(packageInfo.packageName) != null)
                    appList.add(new AppInfo(packageInfo.packageName,
                            packageInfo.applicationInfo.loadLabel(getPackageManager()).toString(), packageInfo.applicationInfo.uid, 0, 0));
            }
        }
        else{
            appList = new ArrayList<>(FloatingService.packageList);
        }
        adapter = new PackageListAdapter(PackageListActivity.this, R.layout.app_item, appList,getPackageManager());
        listView.setAdapter(adapter);
    }

    public void getSearchPackage(String searchPackageName){
        for(int i = 0;i<appNames.length;i++){
            if(appNames[i].equals(searchPackageName)){
                listView.smoothScrollToPosition(i);
                break;
            }
        }
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
