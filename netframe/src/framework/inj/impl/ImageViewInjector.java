package framework.inj.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;

import com.nostra13.universalimageloader.core.ImageLoader;

import framework.inj.ImageViewInj;
import framework.net.image.ImageUploader;
import framework.net.image.Uploadable;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author ss
 * supported type
 */
public class ImageViewInjector extends ViewInjector{

	@Override
	public boolean addParams(View view, HashMap<String, String> params, Object bean, Field field)
			throws Exception{
		if(view instanceof ImageView){
			Annotation anno = field.getAnnotation(ImageViewInj.class);
			ImageViewInj imageInj = (ImageViewInj) anno;
			String uploadUrl = imageInj.value();
			if(bean instanceof Uploadable){
				ImageUploader.upload(uploadUrl, ((Uploadable) bean).getFileUrl());
			}else{
				BitmapDrawable drawable = (BitmapDrawable) ((ImageView) bean).getDrawable();
				ImageUploader.upload(uploadUrl, drawable.getBitmap());
			}
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean setContent(Context context, View view, Object bean, Field field) 
			throws Exception{
		if(view instanceof ImageView){
			Object value = field.get(bean);
			String str = getString(value);
			ImageLoader.getInstance().displayImage(str,
					(ImageView) view);
			return true;
		}else{
			return false;
		}
	}

}
