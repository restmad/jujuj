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
import com.tencent.mm.sdk.modelmsg.WXTextObject;
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

public class WechatShare extends Share{

	private IWXAPI apiWechat;
	private static final int THUMB_SIZE = 100;
	private final String APP_ID = "wx8e194b83c46700ec";
    
	public WechatShare(final Context context) {
		super(context);
		apiWechat = WXAPIFactory.createWXAPI(context, APP_ID, true);
		apiWechat.registerApp(APP_ID);
	}

	public void share(final Bitmap bmp, final Tag tag) {
		WXImageObject imgObj = new WXImageObject(bmp);//new WXTextObject(holder.tag.getTitle());//
		
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;

		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
		msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp); 
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction(null);
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		apiWechat.sendReq(req);
		
        System.out.println("share:"+tag.getTitle());
    }

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : 
			type + System.currentTimeMillis();
	}

	@Override
	public int getId() {
		return R.id.select_share_wechat;
	}
	
}
