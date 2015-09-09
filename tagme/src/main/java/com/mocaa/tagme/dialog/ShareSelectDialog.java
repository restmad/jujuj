package com.mocaa.tagme.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.mocaa.tagme.R;
import com.mocaa.tagme.db.UserPref;

public class ShareSelectDialog  extends Dialog {
	
	private static ShareSelectDialog customProgressDialog = null;
	private static View wechatBtn;
	
	public ShareSelectDialog(Context context){
		super(context);
	}
	
	public ShareSelectDialog(Context context, int theme) {
        super(context, theme);
    }  
	
	public static ShareSelectDialog createDialog(Context context){
		customProgressDialog = new ShareSelectDialog(
				context, R.style.customProgressDialog);
		customProgressDialog.setContentView(R.layout.dialog_select_share);
		customProgressDialog.findViewById(R.id.select_share_wechat)
				.setOnClickListener(mOnClickListener);
		customProgressDialog.findViewById(R.id.select_share_weibo)
				.setOnClickListener(mOnClickListener);
		customProgressDialog.findViewById(R.id.select_share_qzone)
				.setOnClickListener(mOnClickListener);
		customProgressDialog.findViewById(R.id.select_share_renren)
				.setOnClickListener(mOnClickListener);
		if(UserPref.getUserAccount(context).equals("s@s.s")){
			View deleteBtn = customProgressDialog.findViewById(R.id.admin_delete);
			deleteBtn.setVisibility(View.VISIBLE);
			deleteBtn.setOnClickListener(mOnClickListener);
		}
		
		customProgressDialog.setCanceledOnTouchOutside(true);
		
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		
		return customProgressDialog;
	}
	
	private static View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			if(mDoneBtnListener != null){
				mDoneBtnListener.onClicked(v.getId());
			}
			customProgressDialog.dismiss();
		}
		
	};
	
	private static BtnListener mDoneBtnListener;
	public interface BtnListener{
		public void onClicked(int id);
	}
	public static ShareSelectDialog setOnListener(BtnListener l){
		mDoneBtnListener = l;
		return customProgressDialog;
	}
 
}