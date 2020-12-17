package com.smc.quicker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smc.quicker.R;
import com.smc.quicker.entity.AppInfo;
import com.smc.quicker.view.FloatingView;

import java.util.List;

public class AppStartListAdapter extends BaseAdapter {
    private List<AppInfo> appInfoList;
    private LayoutInflater layoutInflater;
    private PackageManager pm;
    private int emptyBackColor;

    public AppStartListAdapter(Context context, List<AppInfo> appInfoList, PackageManager pm) {
        this.appInfoList = appInfoList;
        layoutInflater = LayoutInflater.from(context);
        this.pm = pm;
        emptyBackColor = context.getResources().getColor(R.color.light_gray);
    }

    @Override
    public int getCount() {
        return appInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return appInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_appinfo_list, null);
            holder = new ViewHolder();
            holder.appName = convertView.findViewById(R.id.grid_app_name);
            holder.appImage = convertView.findViewById(R.id.grid_app_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AppInfo appInfo = appInfoList.get(position);
        if (appInfo != null) {
            if(appInfo.getAppName().length()>10)
                holder.appName.setText(appInfo.getAppName().substring(0,10)+"..");
            else
                holder.appName.setText(appInfo.getAppName());
            try {
                holder.appImage.setImageDrawable(pm.getApplicationIcon(appInfo.getPackageName()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            convertView.setBackgroundColor(emptyBackColor);
            holder.appName.setHeight(FloatingView.px2dp(75));
        }
        return convertView;
    }

    class ViewHolder {
        TextView appName;
        ImageView appImage;
    }

}
