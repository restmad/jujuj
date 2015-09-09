package com.mocaa.tagme.share;

import com.mocaa.tagme.R;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.homepage.HomeViewHolder;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.Toast;

public class WeiboShare extends Share implements IWeiboHandler.Response{

	private final String APP_KEY = "4243487627";

    private IWeiboShareAPI  mWeiboShareAPI = null;
    
	public WeiboShare(final Context context) {
		super(context);	
        
	}

	public void share(final Bitmap bm, final Tag tag) {

        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, APP_KEY);
        

        boolean isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
        

        if (!isInstalledWeibo) {
            mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
                @Override
                public void onCancel() {
                    Toast.makeText(context, 
                            R.string.weibosdk_demo_cancel_download_weibo, 
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        mWeiboShareAPI.registerApp();


        WeiboMessage weiboMessage = new WeiboMessage();
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bm);
        weiboMessage.mediaObject = imageObject;
        

        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();

        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;
        

        mWeiboShareAPI.sendRequest(request);
    }



    @Override 
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
            Toast.makeText(context, R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
            Toast.makeText(context, R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
            Toast.makeText(context, 
                    context.getResources().getString(
                    		R.string.weibosdk_demo_toast_share_failed) 
                    		+ "Error Message: " + baseResp.errMsg, 
                    Toast.LENGTH_LONG).show();
            break;
        }
    }
	@Override
	public int getId() {
		return R.id.select_share_weibo;
	}

}
