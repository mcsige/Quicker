package com.smc.quicker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smc.quicker.adapter.AppSaveListAdapter;
import com.smc.quicker.entity.AppInfo;
import com.smc.quicker.service.FloatingService;
import com.smc.quicker.R;
import com.smc.quicker.util.DBHelper;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static int IN_SELECTED = -1;
    private ArrayList<AppInfo> appList;
    RecyclerView mRecyclerView;
    AppSaveListAdapter adapter;
    private DBHelper dbHelper;    //用于创建帮助器对象（处理数据库相关操作）
    private SQLiteDatabase database;    //用于创建数据库对象
    public static int selectedItem = IN_SELECTED;
    private int order;
    private LinearLayoutManager mLayoutManager;
    private int lastOffset;
    public static int lastPosition;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //底部菜单
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_up, R.id.navigation_delete, R.id.navigation_down)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        //------------------------------------------------

        navView.setOnNavigationItemSelectedListener(item -> {
            if(selectedItem>=0) {
                database = dbHelper.getWritableDatabase();
                switch (item.toString()) {
                    case "上移":
                        if(selectedItem!=0) {
                            int tmp = appList.get(selectedItem).getAppOrder();
                            dbHelper.onUpdateOrder(database, appList.get(selectedItem).getUid(), appList.get(selectedItem - 1).getAppOrder());
                            dbHelper.onUpdateOrder(database, appList.get(selectedItem-1).getUid(), tmp);
                            selectedItem--;
                        }
                        break;
                    case "删除":
                        dbHelper.onDelete(database, new int[]{appList.get(selectedItem).getUid()});
                        FloatingService.count();
                        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        break;
                    case "下移":
                        if(selectedItem!=appList.size()-1) {
                            int tmp = appList.get(selectedItem).getAppOrder();
                            dbHelper.onUpdateOrder(database, appList.get(selectedItem).getUid(), appList.get(selectedItem + 1).getAppOrder());
                            dbHelper.onUpdateOrder(database, appList.get(selectedItem+1).getUid(), tmp);
                            selectedItem++;
                        }
                        break;
                }
                database.close();
                getAppInfoList();
            }
            return true;
        });

        Button openListBtn = findViewById(R.id.open_list);
        dbHelper = new DBHelper(MainActivity.this, "appinfo.db", null, 3);//创建帮助器对象
        openListBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, PackageListActivity.class);
            startActivityForResult(intent,1);
        });
        mRecyclerView = findViewById(R.id.saveAppList);
        startFloatingService();
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    @Override
    protected void onStart() {
        getAppInfoList();
        super.onStart();
    }

    public void startFloatingService() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        } else {
            if(!FloatingService.isRunService(this)) {
                startService(new Intent(this, FloatingService.class));
            }
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
        appList = new ArrayList<>();
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
        initListView();
    }

    public void initListView(){
        getPositionAndOffset();
        adapter = new AppSaveListAdapter(getPackageManager(),appList,getResources(),this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
        scrollToPosition();
    }

    /** * 记录RecyclerView当前位置 */
    private void getPositionAndOffset() {
        if(mLayoutManager!=null) {
            View topView = mLayoutManager.getChildAt(0);
            if (topView != null) {
                //获取与该view的顶部的偏移量
                lastOffset = topView.getTop();
                //得到该View的数组位置
                lastPosition = mLayoutManager.getPosition(topView);
            }
        }
    }

    /** * 让RecyclerView滚动到指定位置 */
    private void scrollToPosition() {
        if(mRecyclerView.getLayoutManager() != null && lastPosition >= 0) {
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(lastPosition, lastOffset);
        }
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
                getAppInfoList();
                selectedItem = IN_SELECTED;
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