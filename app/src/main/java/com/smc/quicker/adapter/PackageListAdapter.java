package com.smc.quicker.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smc.quicker.R;

import java.util.List;

public class PackageListAdapter extends ArrayAdapter<PackageInfo> {

    private int resourceId;
    private PackageManager pm;

    public PackageListAdapter(@NonNull Context context, int resource, @NonNull List<PackageInfo> objects, PackageManager pm) {
        super(context, resource, objects);
        resourceId = resource;
        this.pm = pm;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        PackageInfo packageInfo = getItem(position); // 获取当前项的package实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.appImage = view.findViewById(R.id.app_image);
            viewHolder.appName = view.findViewById(R.id.app_name);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.appImage.setImageDrawable(packageInfo.applicationInfo.loadIcon(pm));
        viewHolder.appName.setText(packageInfo.applicationInfo.loadLabel(pm).toString());
        return view;
    }

    class ViewHolder {
        ImageView appImage;
        TextView appName;
    }
}
