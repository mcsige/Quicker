package com.smc.quicker.adapter;

import android.app.Service;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.smc.quicker.entity.AppInfo;
import com.smc.quicker.util.DBHelper;

import java.util.Collections;
import java.util.List;

public class ItemDragHelper extends ItemTouchHelper.Callback {

    private RecyclerView.Adapter adapter;
    private List<AppInfo> appList;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;
    private int fromPos,toPos;

    public ItemDragHelper(RecyclerView.Adapter adapter, List<AppInfo> appList, DBHelper dbHelper, Context context){
        this.adapter = adapter;
        this.appList = appList;
        this.dbHelper = dbHelper;
        this.context = context;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.LEFT;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        //得到当拖拽的viewHolder的Position
        int fromPosition = viewHolder.getAdapterPosition();
        //拿到当前拖拽到的item的viewHolder
        int toPosition = target.getAdapterPosition();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(appList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(appList, i, i - 1);
            }
        }
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        database = dbHelper.getWritableDatabase();
        dbHelper.onDelete(database,new String[]{appList.get(position).getPackageName()});
        database.close();
        appList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    /**
     * 长按选中Item的时候开始调用
     *
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
            fromPos = viewHolder.getAdapterPosition();
        }
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            //获取系统震动服务
            if(actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
                Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                vib.vibrate(50);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 手指松开的时候还原
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(0);
        toPos = viewHolder.getAdapterPosition();
        database = dbHelper.getWritableDatabase();
        //位置已经在move中变化
        dbHelper.onUpdateOrder(database,appList.get(toPos).getPackageName(),appList.get(fromPos).getAppOrder(),fromPos>toPos);
        database.close();
    }
}
