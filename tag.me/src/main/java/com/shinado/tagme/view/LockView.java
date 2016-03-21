package com.shinado.tagme.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class LockView extends FrameLayout{

	public LockView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private OnTouchListener mTouchListener = new OnTouchListener() {

		private int count = 0;
		private boolean cancel = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			System.out.println("action::"+event.getAction());
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				startAnimation(animDown);
				startCounting();
				break;
			case MotionEvent.ACTION_MOVE:
				if(++count == 5){
					startAnimation(animUp);
					cancel();
					cancel = true;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				startAnimation(animUp);
				cancel();
				reset();
				break;
			case MotionEvent.ACTION_UP:
				if(!cancel){
//					startAnimation(animUp);
//					fireClick();
					stopCounting();
				}
				reset();
				break;
			}
			return true;
		}
		private void reset(){
			count = 0;
			cancel = false;
		}
	};

	private Animation animDown, animUp;
	
	private void initAnim(){
		animDown = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, -0.05f, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0);
		
		animDown.setDuration(80);
		animDown.setFillAfter(true);
		
		animUp = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, -0.05f, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0);
		animUp.setDuration(80);
		animUp.setFillAfter(true);
	}

	private long mTouchDownTime;
	private boolean mStartCounting = false;
	private boolean mCancel = false;
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
						System.out.println("cancel le");
						stopCounting();
						mCancel = false;
						return;
					}
					if(System.currentTimeMillis() - mTouchDownTime >= LONG_TOUCH){
						stopCounting();
						mHandler.sendEmptyMessage(FLAG_LONG_CLICK);
						return;
					}
					try {
						sleep(LONG_TOUCH/10);
					} catch (InterruptedException e) {
					}
				}
				mHandler.sendEmptyMessage(FLAG_CLICK);
			}
		}.start();
	}
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case FLAG_CLICK:
				fireClick();
				break;
			case FLAG_LONG_CLICK:
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

	public interface OnClickListener{
		public void onClick(View v, int flag);
	}
	private OnClickListener mOnClickListener;
	public void setOnClickListener(OnClickListener l){
		this.mOnClickListener = l;
	}

	
}
