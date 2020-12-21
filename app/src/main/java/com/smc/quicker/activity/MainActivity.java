package com.smc.quicker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smc.quicker.adapter.ItemDragHelper;
import com.smc.quicker.adapter.AppSaveListAdapter;
import com.smc.quicker.entity.AppInfo;
import com.smc.quicker.service.FloatingService;
import com.smc.quicker.R;
import com.smc.quicker.util.DBHelper;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static ArrayList<AppInfo> appList;
    private RecyclerView mRecyclerView;
    static AppSaveListAdapter adapter;
    private DBHelper dbHelper;    //用于创建帮助器对象（处理数据库相关操作）
    private SQLiteDatabase database;    //用于创建数据库对象
    private static int order;
    private LinearLayoutManager mLayoutManager;
    private static LoadingDailog dialog;
    private ItemTouchHelper itemTouchHelper;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(MainActivity.this)
                .setMessage("加载中...")
                .setCancelable(false)
                .setCancelOutside(false);
        dialog = loadBuilder.create();
        startFloatingService();
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        Button openListBtn = findViewById(R.id.open_list);
        dbHelper = new DBHelper(MainActivity.this, "appinfo.db", null, 3);//创建帮助器对象
        openListBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, PackageListActivity.class);
            startActivityForResult(intent,1);
        });
        mRecyclerView = findViewById(R.id.saveAppList);
        appList = new ArrayList<>();
        getAppInfoList();
        initListView();
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    public void startFloatingService() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        } else {
            if(!FloatingService.isRunService(this)) {
                dialog.show();
                startService(new Intent(this, FloatingService.class));
            }
        }
    }

    public static void dialogDismiss(){
        if(dialog!=null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(MainActivity.this, FloatingService.class));
            }
        }else if(requestCode==1 && resultCode==RESULT_OK){
            AppInfo appInfo = data.getParcelableExtra("appinfo");
            try {
                database = dbHelper.getWritableDatabase();
                dbHelper.onInsert(database,appInfo,order);
                database.close();
                adapter.insert(appInfo);
                getAppInfoList();
                FloatingService.count();
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "已添加过此应用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getAppInfoList() {//获取全部数据
        database = dbHelper.getWritableDatabase();
        Cursor cursor = dbHelper.onList(database);
        appList.clear();
        if (cursor.moveToFirst()) {
            do {
                AppInfo appInfo1 = new AppInfo();
                appInfo1.setUid(Integer.parseInt(cursor.getString(cursor.getColumnIndex(AppInfo.KEY_uid))));
                appInfo1.setAppName(cursor.getString(cursor.getColumnIndex(AppInfo.KEY_appName)));
                appInfo1.setPackageName(cursor.getString(cursor.getColumnIndex(AppInfo.KEY_packageName)));
                appInfo1.setTimes(Integer.parseInt(cursor.getString(cursor.getColumnIndex(AppInfo.KEY_times))));
                appInfo1.setAppOrder(Integer.parseInt(cursor.getString(cursor.getColumnIndex(AppInfo.KEY_appOrder))));
                appList.add(appInfo1);
            } while (cursor.moveToNext());
        }
        database.close();
        if(appList.size()>0)
            order = appList.get(appList.size()-1).getAppOrder()+1;
        else
            order = 0;
    }

    public void initListView(){
        adapter = new AppSaveListAdapter(getPackageManager(),appList,this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        itemTouchHelper = new ItemTouchHelper(new ItemDragHelper(adapter,appList,dbHelper,this));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public static void updateTimes(int position){
        adapter.updateTimes(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //四个参数的含义:
        // 1.group的id;2.item的id;3.是否排序;4.将要显示的内容
        menu.add(0, 1, 0, "清空");
        menu.add(0, 2, 0, "设置");
        return true;
    }

    //菜单添加点击事件,需要重写onOptionsItemSelected（）方法。
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                database = dbHelper.getWritableDatabase();
                dbHelper.onClear(database);
                database.close();
                adapter.clear();
                getAppInfoList();
                FloatingService.count();
                FloatingService.curPage = 0;
                break;
            case 2:
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}