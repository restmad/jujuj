package com.mocaa.tagme.share;

import com.mocaa.tagme.R;
import com.mocaa.tagme.dialog.LoadingDialog;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.homepage.HomeViewHolder;
import com.renn.sharecomponent.MessageTarget;
import com.renn.sharecomponent.RennShareComponent;
import com.renn.sharecomponent.ShareMessageError;
import com.renn.sharecomponent.RennShareComponent.SendMessageListener;
import com.renn.sharecomponent.message.RennImageMessage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

public class RenrenShare extends Share{

	private RennShareComponent shareComponent;
	private AsyncLoader aLoader;
	private LoadingDialog dialog;
	
	private static final String APP_ID = "272651";
	private static final String API_KEY = "9e9f395f32d141ba99a0613f378f6cbc";
	private static final String SECRET_KEY = "4369568b173b4275b62462d502126c93";
	
	public RenrenShare(Context context) {
		super(context);
		aLoader = new AsyncLoader(context);
		shareComponent = RennShareComponent.getInstance(context);
		shareComponent.init(APP_ID, API_KEY, SECRET_KEY);
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return R.id.select_share_renren;
	}

	@Override
	public void share(final Bitmap bm, final Tag tag) {
		showDialog(context.getResources().getString(R.string.uploading));
		aLoader.uploadImage("upload_img.php", bm, new NetworkCallback() {
			
			@Override
			public void onLoaded(Object obj) {
				String url = (String) obj;
				RennImageMessage message = new RennImageMessage();		
				message.setThumbData(bm);
				message.setImageUrl(url);
				message.setTitle(tag.getTitle());
				shareComponent.setSendMessageListener(new SendMessageListener() {			
					@Override
					public void onSendMessageSuccess(String messageKey, Bundle bundle) {
						Toast.makeText(context, "鍙戦�鎴愬姛" + messageKey, 
								Toast.LENGTH_LONG).show();
					}				
					@Override
					public void onSendMessageFailed(String messageKey, ShareMessageError e) {
						Toast.makeText(context, "error: "+ e.getCode() + ":" + e.getMessage(), 
								Toast.LENGTH_LONG).show();
					}			
					@Override
					public void onSendMessageCanceled(String messageKey) {
						Toast.makeText(context, "鍙戦�鍙栨秷" + messageKey,
								Toast.LENGTH_LONG).show();
					}
				});
				shareComponent.sendMessage(message, MessageTarget.TO_RENREN);
				if(dialog != null){
					dialog.dismiss();
					dialog = null;
				}
			}
		});
		
		
	}

	private void showDialog(String text){
		if(dialog == null){
			dialog = LoadingDialog.createDialog(context);
		}
		dialog.setTipText(text);
		dialog.show();
	}

}
