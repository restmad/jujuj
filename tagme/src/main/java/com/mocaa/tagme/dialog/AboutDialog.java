package com.mocaa.tagme.dialog;

import com.mocaa.tagme.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutDialog extends Dialog {
	
	private static AboutDialog customProgressDialog = null;
	
	public AboutDialog(Context context){
		super(context);
	}
	
	public AboutDialog(Context context, int theme) {
        super(context, theme);
    }  
	
	public static AboutDialog createDialog(Context context){
		customProgressDialog = new AboutDialog(
				context, R.style.customProgressDialog);
		customProgressDialog.setContentView(R.layout.dialog_about);
		customProgressDialog.setCanceledOnTouchOutside(true);
		
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		
		return customProgressDialog;
	}
	
 
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
    	
    	//TODO start
    }
    
}

