package com.smc.quicker.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.smc.quicker.R;
import com.smc.quicker.entity.AppInfo;
import com.smc.quicker.service.FloatingService;
import com.smc.quicker.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

import static com.smc.quicker.service.FloatingService.row;
import static com.smc.quicker.service.FloatingService.col;

/**
 * @Author: wuchaowen
 * @Description:
 * @Time:
 **/
public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private List<AppInfo> appList;
    private LayoutInflater mInflater;
    private PackageManager pm;
    private Context context;
    private static DBHelper dbHelper;    //用于创建帮助器对象（处理数据库相关操作）
    private static SQLiteDatabase database;    //用于创建数据库对象

    public ViewPagerAdapter(Context context, List<AppInfo> data, PackageManager pm) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.appList = data;
        this.pm = pm;
        dbHelper = new DBHelper(context, "appinfo.db", null, 3);//创建帮助器对象
    }

    @NonNull
    @Override
    public ViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.start_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerAdapter.ViewHolder holder, int position) {
        holder.gridView.setOnItemClickListener((parent, view, pos, id) -> {
            AppInfo selectedAppinfo = appList.get(pos);
            if(selectedAppinfo!=null) {
                Intent intent = pm.getLaunchIntentForPackage(selectedAppinfo.getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//重要
                if(intent!=null) {
                    database = dbHelper.getWritableDatabase();
                    dbHelper.onUpdateTimes(database, selectedAppinfo.getUid());
                    database.close();
                    context.startActivity(intent);
                }
                else{
                    Toast.makeText(context, "打开应用失败，是否已卸载", Toast.LENGTH_SHORT).show();
                }
            }
            FloatingService.removeView();
        });
        holder.gridView.setNumColumns(col);
        getShortcut(holder,position);
    }

    @Override
    public int getItemCount() {
        return appList==null?0:appList.size()%(col* row)==0
                ?appList.size()/(col* row):appList.size()/(col* row)+1;
    }

    private void getShortcut(@NonNull ViewPagerAdapter.ViewHolder holder,int position){
        List<AppInfo> gridAppList = appList.subList(position*col*row,(position+1)*col*row);
        AppStartListAdapter adapter = new AppStartListAdapter(holder.gridView,row,context, gridAppList,pm);
        holder.gridView.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        GridView gridView;
        RelativeLayout relativeLayout;

        ViewHolder(View itemView) {
            super(itemView);
            gridView = itemView.findViewById(R.id.gridview);
            relativeLayout = itemView.findViewById(R.id.float_container);
        }
    }
}

