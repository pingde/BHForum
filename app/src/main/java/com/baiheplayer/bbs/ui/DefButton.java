package com.baiheplayer.bbs.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;

import com.baiheplayer.bbs.R;

/**
 * 自定义样式的按钮
 * Created by Administrator on 2017/1/3.
 */

public class DefButton extends Button {

    private Paint paint;

    public DefButton(Context context) {
        super(context);
        initPaint();
    }

    public DefButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public DefButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.color3));
        paint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();
        canvas.drawRect(0,0,width,height,paint);
    }
}
