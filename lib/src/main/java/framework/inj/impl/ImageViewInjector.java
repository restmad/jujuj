package framework.inj.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

import framework.inj.ImageViewInj;
import framework.net.image.AbsImageProvider;
import framework.net.image.Uploadable;

/**
 * 
 * @author ss
 * supported type
 */
public class ImageViewInjector extends ViewInjector {

	private AbsImageProvider mProvider;

	public ImageViewInjector(AbsImageProvider provider){
		mProvider = provider;
	}

	@Override
	public String addParams(View view, final HashMap<String, String> params, final Object bean, final Field field)
			throws Exception{
		if(view instanceof ImageView){
			Annotation anno = field.getAnnotation(ImageViewInj.class);
			final ImageViewInj imageInj = (ImageViewInj) anno;
			final String uploadUrl = imageInj.value();
			final String fieldUrl = (String) field.get(bean);
			if(bean instanceof Uploadable){
				new Thread(){
					public void run(){
						Uploadable uploadable = (Uploadable) bean;
						Bundle b1 = new Bundle(uploadable, "", field.getName());
						mHandler.obtainMessage(WHAT_START_UPLOAD, b1).sendToTarget();
						
						String response = "";
						InputStream is;
						if(fieldUrl != null){
							try {
								is = new FileInputStream(fieldUrl);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
								return;
							}
						}else{
							BitmapDrawable drawable = (BitmapDrawable) ((ImageView) bean).getDrawable();
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							drawable.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
							is = new ByteArrayInputStream(baos.toByteArray());
						}

						mProvider.upload(uploadUrl, params, is);
						Bundle b2 = new Bundle(uploadable, response, field.getName());
						mHandler.obtainMessage(WHAT_UPLOAD_FINISH, b2).sendToTarget();
					}
				}.start();
			}
			return "";
		}else{
			return null;
		}
	}
	
	class Bundle{
		Uploadable uploadable;
		String response;
		String fieldName;
		public Bundle(Uploadable uploadable, String response, String fieldName) {
			this.uploadable = uploadable;
			this.response = response;
			this.fieldName = fieldName;
		}
	}
	
	private static final int WHAT_START_UPLOAD = 1;
	private static final int WHAT_UPLOAD_FINISH = 2;
	
	private static Handler mHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg){
			Bundle data = (Bundle) msg.obj;
			switch(msg.what){
			case WHAT_START_UPLOAD:
				data.uploadable.onUploading(data.fieldName);
				break;
			case WHAT_UPLOAD_FINISH:
				data.uploadable.onUploaded(data.fieldName, data.response);
				break;
			}
		}
	};


	@Override
	public boolean setContent(Context context, View view, Object bean, String name, Object value){
		if(view instanceof ImageView){

			if (value instanceof Integer){
				((ImageView) view).setImageResource((Integer) value);
			}else {
				String url = getString(value);
				//TODO think about it
				mProvider.displayImage(url, (ImageView) view);
//				ImageLoader.getInstance().displayImage(url, (ImageView) view);
			}
			return true;
		}else{
			return false;
		}
	}

}
