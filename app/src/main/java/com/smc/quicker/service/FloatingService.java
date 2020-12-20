package com.smc.quicker.service;

import android.accessibilityservice.AccessibilityService;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.smc.quicker.R;
import com.smc.quicker.activity.MainActivity;
import com.smc.quicker.adapter.AppStartListAdapter;
import com.smc.quicker.adapter.ViewPagerAdapter;
import com.smc.quicker.entity.AppInfo;
import com.smc.quicker.receiver.VolumeBroadcastReceiver;
import com.smc.quicker.util.DBHelper;
import com.smc.quicker.util.SharedPreferencesHelper;
import com.smc.quicker.view.FloatingView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class FloatingService extends AccessibilityService {

    private int x,y,downX,downY;
    private FloatingView view;
    private static View view_main;
    private static WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams,layoutParams_main;
    private static boolean flag;
    private Display display;
    private GridView mGridView;
    private AppStartListAdapter adapter;
    private static DBHelper dbHelper;    //用于创建帮助器对象（处理数据库相关操作）
    private static SQLiteDatabase database;    //用于创建数据库对象
    private ArrayList<AppInfo> appList;
    public static int curPage = 0;
    private static int totalPage;
    public static int row;
    public static int col;
    private static SharedPreferencesHelper helper;
    private VolumeBroadcastReceiver receiver;
    private int width,height;
    private ViewPager2 viewPager2;

    @Override
    public void onCreate() {
        showFloatingWindow();
        registerReceiver();
        super.onCreate();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            helper = new SharedPreferencesHelper(this);
            // 获取WindowManager服务
            dbHelper = new DBHelper(this, "appinfo.db", null, 3);//创建帮助器对象
            updateSetting();
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            display = windowManager.getDefaultDisplay();
            width = display.getWidth();
            height = display.getHeight();
            count();
            // 新建悬浮窗控件
            view = new FloatingView(this);
            layoutParams = new WindowManager.LayoutParams();
            // 设置LayoutParam
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.width = view.width;
            layoutParams.height = view.height;
            layoutParams.x = 0;
            layoutParams.y = 0;
            // 当悬浮窗显示的时候可以获取到焦点
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE //不耽误返回键
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            layoutParams.format = PixelFormat.RGBA_8888;
            view_main = LayoutInflater.from(this).inflate(R.layout.float_ball_main, null);
            layoutParams_main = new WindowManager.LayoutParams();
            // 设置LayoutParam
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams_main.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams_main.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams_main.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams_main.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams_main.x = -layoutParams_main.width/2;
            layoutParams_main.y = -layoutParams_main.height/2;
            // 当悬浮窗显示的时候可以获取到焦点
            layoutParams_main.flags = WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            layoutParams_main.format = PixelFormat.RGBA_8888;
            view_main.setOnClickListener(v -> {
                if(windowManager!=null) {
                    windowManager.removeView(view_main);
                    flag = false;
                }
            });
//            Button lastPageBtn = view_main.findViewById(R.id.last_page_btn);
//            Button nextPageBtn = view_main.findViewById(R.id.next_page_btn);
//            lastPageBtn.setOnClickListener(v -> {
//                curPage = curPage==0?0:curPage-1;
//                getShortcut();
//            });
//            nextPageBtn.setOnClickListener(v -> {
//                curPage = curPage+1==totalPage?curPage:curPage+1;
//                getShortcut();
//            });
            view.setOnTouchListener((v, event) -> {
                display = windowManager.getDefaultDisplay();
                width = display.getWidth();
                height = display.getHeight();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        downX = x;
                        downY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int nowX = (int) event.getRawX();
                        int nowY = (int) event.getRawY();
                        int movedX = nowX - x;
                        int movedY = nowY - y;
                        x = nowX;
                        y = nowY;
                        layoutParams.x = layoutParams.x + movedX;
                        layoutParams.y = layoutParams.y + movedY;

                        // 更新悬浮窗控件布局
                        windowManager.updateViewLayout(v, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(windowManager!=null) {
                            int releaseX = (int)event.getRawX();
                            int releaseY = (int)event.getRawY();
                            double dis = (downX-releaseX)*(downX-releaseX)+(downY-releaseY)*(downY-releaseY);
                            if(Math.sqrt(dis)<5.0 && !flag) {
                                windowManager.addView(view_main, layoutParams_main);
                                flag = true;
                                initView();
                            }
                            if (releaseX < width-releaseX) {
                                SmoothToHide(layoutParams.x,-width/2);
                            }
                            else {
                                SmoothToHide(layoutParams.x,width/2);
                            }
                        }
                        break;
                    default:
                        break;
                }
                return false;
            });

            // 将悬浮窗控件添加到WindowManager
            windowManager.addView(view, layoutParams);
            SmoothToHide(layoutParams.x,-width/2);
        }
    }

    private void SmoothToHide(int from,int to) {
            int mScreenWidth = display.getWidth();
            int mScreenHeight = display.getHeight();
            // 通过属性动画做最后的效果，右侧滑进到左侧，contentView 的页面从右侧开始向左侧滑动显示，那么 right 始终保持是屏幕的宽度不变，改变的是 left 属性，
            //从屏幕宽的值一直改变到 0，那属性动画的间隔就出来了，时间设置整体的滑动为 300 ms，那么剩下的距离需要的滑动时间就是 300 * posX / mScreenWidth
            ValueAnimator animator = ValueAnimator.ofInt(from, to).setDuration(600 * Math.abs(to - from) / mScreenWidth);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addUpdateListener(animation -> {
                // 根据变化的值，重新设置 contentView 的布局
                int pos = (int) animation.getAnimatedValue();
                layoutParams.x = pos;
                if(windowManager!=null)
                    windowManager.updateViewLayout(view, layoutParams);
            });
            animator.start();
    }

    private void initView() {
        view_main.setFocusable(true);
        viewPager2 = view_main.findViewById(R.id.viewpager2);
        appList = new ArrayList<>();
        database = dbHelper.getWritableDatabase();
        Cursor cursor = dbHelper.onList(database);
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
        while (appList.size()!=0 && appList.size()%(row*col)!=0)
            appList.add(null);
        viewPager2.setAdapter(new ViewPagerAdapter(this, appList,getPackageManager()));
    }

    public static void removeView(){
        if(windowManager!=null) {
            windowManager.removeView(view_main);
            flag = false;
        }
    }

    public static void count(){
        database = dbHelper.getWritableDatabase();
        int dataCount = dbHelper.getCount(database);
        totalPage = dataCount%(col*row)==0?dataCount/(col*row):dataCount/(col*row)+1;
        database.close();
    }

    public static void updateSetting(){
        int[] rowCol = helper.getRowCol();
        row = rowCol[0];
        col = rowCol[1];
        curPage = 0;
        count();
    }

    public void registerReceiver() {
        receiver = new VolumeBroadcastReceiver(view);
        final IntentFilter homeFilter = new IntentFilter(VolumeBroadcastReceiver.VOLUME_CHANGED_ACTION);
        registerReceiver(receiver, homeFilter);
    }


    /**
     * 判断服务是否在运行
     * @param context
     * @return
     * 服务名称为全路径 例如com.ghost.WidgetUpdateService
     */
    public static boolean isRunService(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (FloatingService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        windowManager.removeView(view);
        windowManager = null;
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
