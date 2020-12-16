package com.smc.quicker.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.smc.quicker.adapter.PackageListAdapter;
import com.smc.quicker.R;
import com.smc.quicker.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class PackageListActivity extends AppCompatActivity {
    private List<PackageInfo> appList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list);
        List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
        appList = new ArrayList<>();
        for(PackageInfo packageInfo : packageInfos){
            if(packageInfo.packageName.equals(this.getPackageName()))
                continue;
            if(getPackageManager().getLaunchIntentForPackage(packageInfo.packageName)!=null)
                appList.add(packageInfo);
        }
        PackageListAdapter adapter = new PackageListAdapter(PackageListActivity.this, R.layout.app_item, appList,getPackageManager());
        ListView listView = findViewById(R.id.mListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            PackageInfo packageInfo = appList.get(position);
            AppInfo appInfo = new AppInfo(packageInfo.packageName,
                    packageInfo.applicationInfo.loadLabel(getPackageManager()).toString(),packageInfo.applicationInfo.uid,0,0);
            Intent intent = new Intent(PackageListActivity.this,MainActivity.class);
            intent.putExtra("appinfo", appInfo);
            setResult(RESULT_OK,intent);
            finish();
        });
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
