package com.smc.quicker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.smc.quicker.R;

public class FloatingView extends View {

    public int height = 80;
    public int width = 80;
    private Paint paint;


    public FloatingView(Context context) {
        super(context);
        paint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(height, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画大圆
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.purple_700_translucence));
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);
        //画小圆圈
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(width / 2, width / 2, (float) (width * 1.0 / 4), paint);
    }
}

