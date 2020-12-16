package com.smc.quicker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.smc.quicker.R;
import com.smc.quicker.activity.MainActivity;
import com.smc.quicker.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppSaveListAdapter extends RecyclerView.Adapter<AppSaveListAdapter.ViewHolder> {

    private PackageManager pm;
    private ArrayList<AppInfo> appInfos;
    private Resources resources;
    private ArrayList<View> views;

    public AppSaveListAdapter(PackageManager pm, ArrayList<AppInfo> appInfos, Resources resources) {
        this.pm = pm;
        this.appInfos = appInfos;
        this.resources = resources;
        views = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.appinfo_item, parent, false);// 实例化展示的view
        v.setOnClickListener(v1 -> {
            for(View i : views){
                i.setBackgroundColor(Color.WHITE);
            }
            MainActivity.selectedItem = views.indexOf(v1);
            v.setBackground(new ColorDrawable(resources.getColor(R.color.red)));
        });
        views.add(v);
        ViewHolder viewHolder = new ViewHolder(v);// 实例化viewholder
        if(MainActivity.selectedItem!=MainActivity.IN_SELECTED && MainActivity.selectedItem==views.size()-1){
            views.get(MainActivity.selectedItem-MainActivity.lastPosition)
                    .setBackground(new ColorDrawable(resources.getColor(R.color.red)));
        }
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo appInfo = appInfos.get(position);
        try {
            holder.appImage.setImageDrawable(pm.getApplicationIcon(appInfo.getPackageName()));//注意数据类型
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        holder.appName.setText(appInfo.getAppName());
        holder.appTimes.setText("最近使用次数:"+appInfo.getTimes());
    }

    @Override
    public int getItemCount() {
        return appInfos == null ? 0 : appInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appImage;
        TextView appName;
        TextView appTimes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appImage = itemView.findViewById(R.id.appinfo_image);
            appName = itemView.findViewById(R.id.appinfo_name);
            appTimes = itemView.findViewById(R.id.appinfo_times);
        }
    }
}
