package cn.ucai.fulicenter.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import cn.ucai.fulicenter.R;


/**
 * Created by Administrator on 2016/9/29.
 */
public class Cercle extends View {
    Integer count,radius,space,focus;
    int focusColor,nomaColor;

    Paint mpaint;

    public Cercle(Context context) {
        super(context);
    }

    public Cercle(Context context , AttributeSet attributeSet){
        super(context,attributeSet);
        initData(context, attributeSet);

    }


    public void setCount(int count){
        this.count=count;
        invalidate();
    }


    private void initData(Context context, AttributeSet attributeSet) {
        TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.Cercle);
        count= array.getInt(R.styleable.Cercle_count,5);
        focus = array.getInt(R.styleable.Cercle_focus, 0);
        radius = array.getDimensionPixelOffset(R.styleable.Cercle_r, 10);
        space = array.getDimensionPixelSize(R.styleable.Cercle_space, 5);
        nomaColor = array.getColor(R.styleable.Cercle_normal_color, 0x000);
        focusColor = array.getColor(R.styleable.Cercle_focus_color, 0xff0000);

        array.recycle();
        mpaint=new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        super.onDraw(canvas);
        mpaint.setAntiAlias(true);
        mpaint.setStyle(Paint.Style.FILL);
        //计算出第一个实心圆的起始横坐标
        int leftSpace = getPaddingLeft() + (getWidth() - count * 2 * radius - space * (count - 1)) / 2;
        for (int i = 0; i < count; i++) {
            //计算每个实心圆的横坐标
            int x = leftSpace + i * (2 * radius + space);
            //计算每个实心圆的颜色
            int color = i == focus ? focusColor : nomaColor;
            mpaint.setColor(color);
            canvas.drawCircle(x + radius, radius, radius, mpaint);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = initWidth(widthMeasureSpec);
        int height =initHeight(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }


    private int initHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int result = size;
        if (mode != MeasureSpec.EXACTLY) {
            size = getPaddingTop() + getPaddingBottom() + radius * 2;
            result = Math.min(result, size);
        }
        return result;
    }

    private int initWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int result = size;
        if (mode != MeasureSpec.EXACTLY) {
            size = getPaddingLeft() + getPaddingRight() + 2 * radius * count + space * (count - 1);
            result = Math.min(result, size);
        }
        return result;
    }
    public void setFocus(int focus) {
        this.focus = focus;
        invalidate();
    }

    public int getFocus() {
        return focus;
    }

    public int getCount() {
        return count;
    }


}
