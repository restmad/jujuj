package com.mocaa.tagme.share;

import java.util.ArrayList;

import org.json.JSONObject;

import com.mocaa.tagme.R;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.dialog.LoadingDialog;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.homepage.HomeViewHolder;
import com.mocaa.tagme.util.BitmapUtil;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class QZoneShare extends Share{

	private Tencent mTencent;
	private static final int THUMB_SIZE = 150;
	private final String APP_ID = "1103376695";
	private LoadingDialog dialog;
	private Context context;
	private AsyncLoader aLoader;
	private UserPref userPref;
    
	public QZoneShare(final Context context) {
		super(context);
		this.context = context;
		userPref = new UserPref(context);
		aLoader = new AsyncLoader(context);
		mTencent = Tencent.createInstance(APP_ID, context.getApplicationContext());
		
		if(userPref.isQzoneLogin()){
			String openid = userPref.getQzoneOId();
			String access_token = userPref.getQzoneToken();
			String expires_in = userPref.getQzoneIN(); 
			mTencent.setOpenId(openid);
			mTencent.setAccessToken(access_token, expires_in);
		}
	}
	
	public void share(final Bitmap bm, final Tag tag) {

		IUiListener listener = new IUiListener() {

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onComplete(Object obj) {
				if(obj instanceof JSONObject){
					try {
						onComplete((JSONObject)obj);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					System.err.println(obj.getClass().getName());
				}
			}
			
			private void onComplete(JSONObject obj) throws Exception{
				String expires_in = obj.getString("expires_in");
				long in = System.currentTimeMillis() +
						Long.parseLong(expires_in) * 1000;
				expires_in = in+"";
				String openid = obj.getString("openid");
				String access_token = obj.getString("access_token");
				userPref.loginQzone();
				userPref.setQzoneIN(expires_in);
				userPref.setQzoneOId(openid);
				userPref.setQzoneToken(access_token);
				mTencent.setAccessToken(access_token, expires_in);
				mTencent.setOpenId(openid);
				
				shareQzone(bm, tag.getTitle());
			}
			
			@Override
			public void onError(UiError arg0) {
				// TODO Auto-generated method stub
				
			}
	 	}; 
	 	if(!userPref.isQzoneLogin()){
		 	mTencent.login((Activity)context, "all", listener);
	 	}else{
	 		shareQzone(bm, tag.getTitle());
	 	}
    }  

	private void shareQzone(Bitmap bm, final String title){
		
		if(bm == null){
			return;
		}
		showDialog(context.getResources().getString(R.string.uploading));
		aLoader.uploadImage("upload_img.php", bm, new NetworkCallback() {
			
			@Override
			public void onLoaded(Object obj) {
				String url = (String) obj;
				System.out.println("upload:"+url);
				ArrayList<String> urList = new ArrayList<String>();
		 		urList.add(url);
			 	Bundle params = new Bundle();
			 	params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, 
			 			QzoneShare.SHARE_TO_QZONE_TYPE_APP );
			    params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
			    params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "");
			    params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);
			    params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urList);
			    mTencent.shareToQzone((Activity)context, params, new IUiListener(){

					@Override
					public void onCancel() {
						Toast.makeText(context, R.string.share_failed,
								Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete(Object obj) {
						if(obj instanceof JSONObject){
							System.out.println("share:"+obj.toString());
						}else{
							System.out.println("share:"+obj.getClass().getName());
							Toast.makeText(context, R.string.share_failed,
									Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onError(UiError error) {
						System.out.println("share:"+error.errorMessage);
						Toast.makeText(context, R.string.share_failed,
								Toast.LENGTH_LONG).show();
					}
			    	
			    });
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

	@Override
	public int getId() {
		return R.id.select_share_qzone;
	}

	
}
