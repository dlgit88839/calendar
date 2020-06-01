package com.example.calendarapp.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class SquareBackGroundLinearLayout extends LinearLayout {

    public SquareBackGroundLinearLayout(Context context) {
        super(context);
    }

    public Drawable shapeDrawable;

    public SquareBackGroundLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareBackGroundLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SquareBackGroundLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
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
        return super.drawChild(canvas, child, drawingTime);
    }


}
