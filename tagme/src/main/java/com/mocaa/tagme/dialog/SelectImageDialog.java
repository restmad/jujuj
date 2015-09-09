package com.mocaa.tagme.dialog;

import com.mocaa.tagme.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectImageDialog extends Dialog {
	
	public static final int SELECT_GALLERY = 1;
	public static final int SELECT_CAMERA = 2;
	private static SelectImageDialog customProgressDialog = null;
	private static ImageButton galleryBtn, cameraBtn;
	private static TextView title;
	
	public SelectImageDialog(Context context){
		super(context);
	}
	
	public SelectImageDialog(Context context, int theme) {
        super(context, theme);
    }  
	  
	public static SelectImageDialog createDialog(Context context){
		customProgressDialog = new SelectImageDialog(
				context, R.style.customProgressDialog);
		customProgressDialog.setContentView(R.layout.dialog_select_img);
		title = (TextView) customProgressDialog.findViewById(R.id.dialog_select_img_title);
		cameraBtn = (ImageButton) customProgressDialog.findViewById(
				R.id.dialog_select_camera);
		cameraBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mDoneBtnListener != null){
					mDoneBtnListener.onClicked(SELECT_CAMERA);
				}
				customProgressDialog.dismiss();
			}
		});
		galleryBtn = (ImageButton) customProgressDialog.findViewById(
				R.id.dialog_select_gallery);
		galleryBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mDoneBtnListener != null){
					mDoneBtnListener.onClicked(SELECT_GALLERY);
				}
				customProgressDialog.dismiss();
			}
		});
		
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		
		return customProgressDialog;
	}
	
	private static BtnListener mDoneBtnListener;
	public interface BtnListener{
		public void onClicked(int id);
	}
	public static SelectImageDialog setOnListener(BtnListener l){
		mDoneBtnListener = l;
		return customProgressDialog;
	}
	public static SelectImageDialog setTitle(String str){
		title.setText(str);
		return customProgressDialog;
	}
 
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
    	
    	//TODO start
    }
    
}

