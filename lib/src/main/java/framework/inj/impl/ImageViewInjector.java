package framework.inj.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import framework.inj.ImageViewInj;
import framework.inj.exception.FieldNotPublicException;
import framework.inj.exception.TypeNotSupportedException;
import framework.net.image.ImageUploader;
import framework.net.image.Uploadable;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

/**
 * 
 * @author ss
 * supported type
 */
public class ImageViewInjector extends ViewInjector{

	@Override
	public String addParams(View view, HashMap<String, String> params, final Object bean, final Field field)
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
						
						String response;
						if(fieldUrl != null){
							response = ImageUploader.upload(uploadUrl, fieldUrl);
						}else{
							BitmapDrawable drawable = (BitmapDrawable) ((ImageView) bean).getDrawable();
							response = ImageUploader.upload(uploadUrl, drawable.getBitmap());
						}
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
	public boolean setContent(Context context, View view, Object bean, Field field) {
		if(view instanceof ImageView){
			Object value = null;
			try {
				value = field.get(bean);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				throw new FieldNotPublicException("The field is not public. In class " +
						bean.getClass().getName() + ", field " + field.getName());
			}
			if (value instanceof Integer){
				((ImageView) view).setImageResource((Integer) value);
			}else {
				String str = getString(value);
				ImageLoader.getInstance().displayImage(str, (ImageView) view);
			}
			return true;
		}else{
			return false;
		}
	}

}
