package com.smc.quicker.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;

import com.smc.quicker.R;

import java.util.Random;

public class FloatingView extends View {

    public int height = 50;
    public int width = 50;
    private Paint paint;


    public FloatingView(Context context) {
        super(context);
        paint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(px2dp(height), px2dp(width));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 随机颜色
        Random random = new Random();
        int randColor = 0xaf000000
                | (random.nextInt(0xaf))<<16
                | (random.nextInt(0xaf))<<8
                | (random.nextInt(0xaf));
        //画大圆
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(randColor);
        canvas.drawCircle(px2dp(width / 2), px2dp(width / 2), px2dp(width / 2), paint);
        //画小圆圈
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(px2dp(width / 2), px2dp(width / 2), px2dp(width / 4), paint);
    }

    public static int px2dp(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, Resources.getSystem().getDisplayMetrics());
    }
}

