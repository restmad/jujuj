package framework.inj.impl;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

import framework.inj.entity.utility.Transformable;
import framework.inj.exception.FieldNotPublicException;
import framework.inj.exception.TypeNotSupportedException;

/**
 * 
 * @author ss
 * supported type String
 */
public class TextViewBinder extends ViewBinder {

	@Override
	public String addParams(View view, HashMap<String, String> params, Object bean, String fieldName, String packageName)
			throws Exception{
		if(view instanceof TextView){
//			if(view.getClass().isAssignableFrom(TextView.class)){
//				return false;
//			}else{
				Object value = ((TextView) view).getText().toString();
				if (bean instanceof Transformable) {
					//get value for model
					Object valueToServer = ((Transformable) bean).toServer(fieldName, value);
					if(valueToServer != null){
						value = valueToServer;
					}
				}
				params.put(fieldName, value+"");
				return value+"";
//			}
		}else{
			return "";
		}
	}

	@Override
	public boolean setContent(Context context, View view, Object bean, String name, Object value, String packageName)
			throws FieldNotPublicException, TypeNotSupportedException {
		if(view instanceof TextView){
			String str = getString(value);
			((TextView) view).setText(str);
			return true;
		}else{
			return false;
		}
	}

}
