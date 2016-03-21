package com.shinado.tagme.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shinado.tagme.R;

public class LoadingDialog extends Dialog {
	
	private static LoadingDialog customProgressDialog = null;
	private static TextView tipText;
	private static LinearLayout groupView;
	
	public LoadingDialog(Context context){
		super(context);
	}
	
	public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }  
	
	public void setTipText(String text){
		tipText.setText(text);
	}

	public static LoadingDialog createDialog(Context context){
		customProgressDialog = new LoadingDialog(
				context, R.style.customProgressDialog);
		customProgressDialog.setContentView(R.layout.dialog_loading);
		tipText = (TextView) customProgressDialog.findViewById(R.id.dialog_loading_hint);
		groupView = (LinearLayout) customProgressDialog.findViewById(R.id.dialog_loading_group);
		startAnimation();
		
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		
		return customProgressDialog;
	}
	
	private static void startAnimation(){
		int size = groupView.getChildCount();
		for(int i=0; i<size; i++){
			View v = groupView.getChildAt(i);
			Animation anim = new ScaleAnimation(
					1, 0, 
					1, 0, 
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			anim.setDuration(900 - i*250);
			anim.setStartOffset(i*250);
			anim.setInterpolator(new AccelerateDecelerateInterpolator());
			anim.setRepeatMode(Animation.REVERSE);
			anim.setRepeatCount(Animation.INFINITE);
			v.startAnimation(anim);
		}
	}
	
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
    	
    	//TODO start
    }
    
}

