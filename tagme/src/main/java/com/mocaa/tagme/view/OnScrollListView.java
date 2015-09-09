package com.mocaa.tagme.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class OnScrollListView extends ListView{
	
	private Context context;

	
	
	public OnScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setOnScrollChangedListener();
	}
	
	private boolean mRolling = false;
	private static final int SCROLL = 1;
	private static final int STOP = 2;
	
	private void stopScrolling(){
		mRolling = false;
	}
	
	private void startScrolling(){
		new Thread(){
			public void run(){
				mRolling = true;
				while(mRolling){
					try {
						sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mHandler.sendEmptyMessage(SCROLL);
				}
				mHandler.sendEmptyMessage(STOP);
			}
		}.start();
	}
	
	private void setOnScrollChangedListener(){
		setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				System.out.println("what scroll:"+scrollState);
				switch(scrollState){
				//start
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					startScrolling();
					break;
				case OnScrollListener.SCROLL_STATE_FLING:
					
					break;
				//stop
				case OnScrollListener.SCROLL_STATE_IDLE:
					stopScrolling();
					break;
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case SCROLL:
				if(mBetterOnScrollListener != null){
					mBetterOnScrollListener.onScroll(OnScrollListView.this);
				}
			}
		}
	};
	
	BetterOnScrollListener mBetterOnScrollListener;
	public void setBetterOnScrollListener(BetterOnScrollListener l){
		this.mBetterOnScrollListener = l;
	}
	public interface BetterOnScrollListener{
		public void onScroll(ListView v);
	}
}
