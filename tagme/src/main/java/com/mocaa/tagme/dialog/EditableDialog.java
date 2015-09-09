package com.mocaa.tagme.dialog;

import com.mocaa.tagme.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class EditableDialog extends Dialog {
	
	public static final int CANCEL = 1;
	public static final int DONE = 2;
	private static EditableDialog customProgressDialog = null;
	private static EditText contentEdit;
	private static TextView title;
	private static Button doneBtn, cancelBtn;
	
	public EditableDialog(Context context){
		super(context);
	}
	
	public EditableDialog(Context context, int theme) {
        super(context, theme);
    }  

	public static EditableDialog setContentText(String text){
		contentEdit.setText(text);
		return customProgressDialog;
	}

	public static EditableDialog setTitle(String text){
		title.setText(text);
		return customProgressDialog;
	}

	public static EditableDialog setDoneButton(String text){
		doneBtn.setText(text);
		return customProgressDialog;
	}

	public static EditableDialog setCancelButton(String text){
		cancelBtn.setText(text);
		return customProgressDialog;
	}
	  
	public static EditableDialog createDialog(Context context){
		customProgressDialog = new EditableDialog(
				context, R.style.customProgressDialog);
		customProgressDialog.setContentView(R.layout.dialog_edit);
		contentEdit = (EditText) customProgressDialog.findViewById(R.id.dialog_edit_content);
		title = (TextView) customProgressDialog.findViewById(R.id.dialog_edit_title);
		
		doneBtn = (Button) customProgressDialog.findViewById(R.id.dialog_edit_done);
		doneBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mDoneBtnListener != null){
					mDoneBtnListener.onClicked(DONE, contentEdit.getText().toString());
				}
				customProgressDialog.dismiss();
			}
		});
		cancelBtn = (Button) customProgressDialog.findViewById(R.id.dialog_edit_cancel);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mDoneBtnListener != null){
					mDoneBtnListener.onClicked(CANCEL, contentEdit.getText().toString());
				}
				customProgressDialog.dismiss();
			}
		});
		
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		
		return customProgressDialog;
	}
	
	private static BtnListener mDoneBtnListener;
	public interface BtnListener{
		public void onClicked(int id, String text);
	}
	public static EditableDialog setOnListener(BtnListener l){
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

