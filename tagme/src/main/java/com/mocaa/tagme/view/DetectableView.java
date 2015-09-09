package com.mocaa.tagme.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class DetectableView extends RelativeLayout{

	public DetectableView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onWindowFocusChanged(boolean b){
		if(onWindowFocusChangedListener != null){
			onWindowFocusChangedListener.onWindowFocusChanged(b);
		}
	}
	
	public OnWindowFocusChangedListener getOnWindowFocusChangedListener() {
		return onWindowFocusChangedListener;
	}

	public void setOnWindowFocusChangedListener(
			OnWindowFocusChangedListener onWindowFocusChangedListener) {
		this.onWindowFocusChangedListener = onWindowFocusChangedListener;
	}

	private OnWindowFocusChangedListener onWindowFocusChangedListener;
	public interface OnWindowFocusChangedListener{
		public void onWindowFocusChanged(boolean b);
	}
}
