package com.mocaa.tagme.util;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import com.mocaa.tagme.dialog.SelectImageDialog;
import com.mocaa.tagme.dialog.SelectImageDialog.BtnListener;
import com.mocaa.tagme.download.ImageLoaderImpl;
import com.mocaa.tagme.global.GlobalDefs;
import com.soundcloud.android.crop.Crop;

public class ImageSelector {
	
	private Context context;
	
	public ImageSelector(Context context){
		this.context = context;
	}

	public void selectGallery(){
		@SuppressWarnings("static-access")
		SelectImageDialog dailog = SelectImageDialog
				.createDialog(context)
				.setOnListener(new BtnListener() {
					@Override
					public void onClicked(int id) {
						switch(id){
						case SelectImageDialog.SELECT_CAMERA:
							tempUri = getTempUri();
//							Crop.pickCamera((Activity) context, tempUri);
							break;
						case SelectImageDialog.SELECT_GALLERY:
							Crop.pickImage((Activity) context);
							break;
						}
					}
				}); 
		dailog.setCanceledOnTouchOutside(true);
		dailog.show();
		
    }  

	private Uri tempUri;
	
	private Uri getTempUri() {

		File root = new File(Environment.getExternalStorageDirectory(),
				"Directory");
		if (!root.exists()) {
			root.mkdirs();
		}
		File file;

		file = new File(root, System.currentTimeMillis()+".jpg");

		Uri uri = Uri.fromFile(file);
		
		return uri;

	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
			if(data != null){
		        beginCrop(data.getData());
			}else{
		        beginCrop(tempUri);
			}
	    }
	}

	private String imgUri;

	private void beginCrop(Uri source) {
		/*
		 * in order to keep loading image in page, 
		 * do a MD5 coding
		 */
		imgUri = System.currentTimeMillis()+".jpg";
	    Uri outputUri = Uri.fromFile(new File(context.getCacheDir(), 
	    		ImageLoaderImpl.getMD5Str(imgUri)));
		int width = (int) (GlobalDefs.getScreenWidth() * 1f);
		Crop.of(source, outputUri).asSquare()
	    		.withMaxSize(width, width)
	    		.start((Activity) context);
	}
	
	public String getImgUri(){
		return imgUri;
	}
}
