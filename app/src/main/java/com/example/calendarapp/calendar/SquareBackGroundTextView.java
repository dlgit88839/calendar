package com.example.calendarapp.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class SquareBackGroundTextView extends TextView {

    public SquareBackGroundTextView(Context context) {
        super(context);
    }

    public Drawable shapeDrawable;

    public SquareBackGroundTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareBackGroundTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SquareBackGroundTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width=MeasureSpec.getSize(widthMeasureSpec);
//        int height=MeasureSpec.getSize(heightMeasureSpec);
//        int square=width<height?width:height;
//        setMeasuredDimension(width,height);

    }

    public void setShapeBackGround(Drawable drawable){

        this.shapeDrawable=drawable;
        invalidate();

    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (shapeDrawable!=null){
            int width=getWidth();
            int height=getHeight();
            int square=(width<height?width:height)-5;//-5防止两个item的背景离的太近
            shapeDrawable.setBounds(0,0,square,square);
            canvas.save();
            canvas.translate((width-square)/2,(height-square)/2);
            shapeDrawable.draw(canvas);
            canvas.restore();
        }
        super.onDraw(canvas);
    }
}
