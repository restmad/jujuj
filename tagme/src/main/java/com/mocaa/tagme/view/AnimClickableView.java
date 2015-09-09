package com.mocaa.tagme.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Created by dell on 2014/10/12.
 */
public abstract class AnimClickableView extends ImageView {
	
	private static final String TAG = "AnimClickableView";

    public AnimClickableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(mTouchListener);
    }

    private OnTouchListener mTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    startAnimation(getAnimDown());
                    startCounting();
                    break;
                case MotionEvent.ACTION_CANCEL:
                	if(!mLongClick){
	                	o("action cancel");
	                    startAnimation(getAnimUp());
	                    cancel();
                	}
                    reset();
                    break;
                case MotionEvent.ACTION_UP:
                	if(!mLongClick){
    					startAnimation(getAnimUp());
                        stopCounting();
                	}
                    reset();
                    break;
            }
            return true;
        }
        private void reset(){
        	o("reset");
            mCancel = false;
            mLongClick = false;
        }
    };

    private long mTouchDownTime;
    private boolean mStartCounting = false;
    private boolean mCancel = false;
    private boolean mLongClick = false;
    private int LONG_TOUCH = 500;

    public static final int FLAG_CLICK = 1;
    public static final int FLAG_LONG_CLICK = 2;

    private void stopCounting(){
        mStartCounting = false;
    }
    private void cancel(){
        mCancel = true;
    }
    private void startCounting(){
        mTouchDownTime = System.currentTimeMillis();
        mStartCounting = true;
        new Thread(){
            public void run(){
                while(mStartCounting){
                    if(mCancel){
                        o("cancel");
                        stopCounting();
                        mCancel = false;
                        return;
                    }
                    if(System.currentTimeMillis() - mTouchDownTime >= LONG_TOUCH){
                        o("long click");
                        stopCounting();
                        mCancel = false;
                        mHandler.sendEmptyMessage(FLAG_LONG_CLICK);
                        return;
                    }
                    try {
                        sleep(LONG_TOUCH/10);
                    } 
                    catch (InterruptedException e) {
                    }
                }
                o("click");
                mHandler.sendEmptyMessage(FLAG_CLICK);
                mCancel = false;
            }
        }.start();
    }
    
    private void o(String s){
    	Log.v(TAG, s);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case FLAG_CLICK:
                    fireClick();
                    break;
                case FLAG_LONG_CLICK:
                	mLongClick = true;
					startAnimation(getAnimUp());
                    fireLongClick();
                    break;
            }
        }
    };

    private void fireClick(){
        if(mOnClickListener != null){
            System.out.println("fire:"+FLAG_CLICK);
            mOnClickListener.onClick(this, FLAG_CLICK);
        }
    }

    private void fireLongClick(){
        if(mOnClickListener != null){
            mOnClickListener.onClick(this, FLAG_LONG_CLICK);
        }
    }

    public interface OnAnimClickListener{
        public void onClick(View v, int flag);
    }
    private OnAnimClickListener mOnClickListener;
    public void setOnAnimClickListener(OnAnimClickListener l){
        this.mOnClickListener = l;
    }

    public abstract Animation getAnimDown();
    public abstract Animation getAnimUp();

}