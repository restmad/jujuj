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

public class NormalDialog extends Dialog {
	
	public static final int CANCEL = 1;
	public static final int DONE = 2;
	private static NormalDialog customProgressDialog = null;
	private static TextView contentText, titleText;
	private static Button doneBtn, cancelBtn;
	
	public NormalDialog(Context context){
		super(context);
	}
	
	public NormalDialog(Context context, int theme) {
        super(context, theme);
    }  

	public static NormalDialog setTitleText(int id){
		titleText.setText(id);
		return customProgressDialog;
	}

	public static NormalDialog setTitleText(String text){
		titleText.setText(text);
		return customProgressDialog;
	}

	public static NormalDialog setContentText(String text){
		contentText.setText(text);
		return customProgressDialog;
	}

	public static NormalDialog setContentText(int res){
		contentText.setText(res);
		return customProgressDialog;
	}

	public static NormalDialog setDoneButton(String text){
		doneBtn.setText(text);
		return customProgressDialog;
	}

	public static NormalDialog setCancelButton(String text){
		cancelBtn.setText(text);
		return customProgressDialog;
	}
	  
	public static NormalDialog createDialog(Context context){
		customProgressDialog = new NormalDialog(
				context, R.style.customProgressDialog);
		customProgressDialog.setContentView(R.layout.dialog_normal);
		contentText = (TextView) customProgressDialog.findViewById(R.id.dialog_normal_content);
		titleText = (TextView) customProgressDialog.findViewById(R.id.dialog_normal_title);
		doneBtn = (Button) customProgressDialog.findViewById(R.id.dialog_normal_done);
		doneBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mDoneBtnListener != null){
					mDoneBtnListener.onClicked(DONE);
				}
				customProgressDialog.dismiss();
			}
		});
		cancelBtn = (Button) customProgressDialog.findViewById(R.id.dialog_normal_cancel);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mDoneBtnListener != null){
					mDoneBtnListener.onClicked(CANCEL);
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
	public static NormalDialog setOnListener(BtnListener l){
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

