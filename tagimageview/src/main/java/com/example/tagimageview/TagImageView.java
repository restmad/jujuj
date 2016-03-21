package com.example.tagimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class TagImageView extends View{

    private ITag mTag;
    private Paint mPaint;
    private Bitmap mBackground;
    private Bitmap mForeground;

    public TagImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBackground, 0, 0, mPaint);
    }

    public void setTag(ITag tag){
        this.mTag = tag;
    }

    public interface ITag{

        public List<ITag> getTags();
        public Bitmap getImage();
        public String getText();
        public Point getLocation();
        public Direction getDirection();

        public enum Direction{
            LEFT, RIGHT
        }
    }
}
