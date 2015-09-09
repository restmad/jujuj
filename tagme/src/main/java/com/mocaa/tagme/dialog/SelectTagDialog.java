package com.mocaa.tagme.dialog;

import com.mocaa.tagme.R;
import com.mocaa.tagme.entity.Tag;

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

public class SelectTagDialog extends Dialog {
	
	private static SelectTagDialog customProgressDialog = null;
	private static View imageBtn, textBtn, tagBtn;
	
	public SelectTagDialog(Context context){
		super(context);
	}
	
	public SelectTagDialog(Context context, int theme) {
        super(context, theme);
    }  
	
	private static Animation getAnimation(int p){
		Animation anim = new ScaleAnimation(0f, 1, 0, 1, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setInterpolator(new OvershootInterpolator());
		anim.setDuration(320);
		anim.setStartOffset(p*40);
		return anim;
	}
	  
	public static SelectTagDialog createDialog(Context context){
		customProgressDialog = new SelectTagDialog(
				context, R.style.customProgressDialog);
		customProgressDialog.setContentView(R.layout.dialog_select_tag);
		imageBtn = customProgressDialog.findViewById(
				R.id.select_tag_img);
		imageBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mDoneBtnListener != null){
					mDoneBtnListener.onClicked(Tag.TYPE_IMAGE);
				}
				customProgressDialog.dismiss();
			}
		});
		textBtn = customProgressDialog.findViewById(
				R.id.select_tag_text);
		textBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mDoneBtnListener != null){
					mDoneBtnListener.onClicked(Tag.TYPE_TEXT);
				}
				customProgressDialog.dismiss();
			}
		});
		tagBtn = customProgressDialog.findViewById(
				R.id.select_tag_tag);
		tagBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mDoneBtnListener != null){
					mDoneBtnListener.onClicked(Tag.TYPE_LOCATION);
				}
				customProgressDialog.dismiss();
			}
		});
		
		customProgressDialog.findViewById(R.id.select_tag_img_img).startAnimation(getAnimation(0));
		customProgressDialog.findViewById(R.id.select_tag_text_img).startAnimation(getAnimation(2));
		customProgressDialog.findViewById(R.id.select_tag_tag_img).startAnimation(getAnimation(1));
		
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		
		return customProgressDialog;
	}
	
	private static BtnListener mDoneBtnListener;
	public interface BtnListener{
		public void onClicked(int id);
	}
	public static SelectTagDialog setOnListener(BtnListener l){
		mDoneBtnListener = l;
		return customProgressDialog;
	}
 
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
    	
    	//TODO start
    }
    
}

