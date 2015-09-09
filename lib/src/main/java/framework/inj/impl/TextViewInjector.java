package framework.inj.impl;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.HashMap;

import framework.inj.entity.utility.Transformable;
import framework.inj.exception.FieldNotPublicException;
import framework.inj.exception.TypeNotSupportedException;

/**
 * 
 * @author ss
 * supported type String
 */
public class TextViewInjector extends ViewInjector {

	@Override
	public String addParams(View view, HashMap<String, String> params, Object bean, Field field)
			throws Exception{
		if(view instanceof TextView){
//			if(view.getClass().isAssignableFrom(TextView.class)){
//				return false;
//			}else{
				String value = ((TextView) view).getText().toString();
				if (bean instanceof Transformable) {
					//get value for model
					String valueToServer = (String) ((Transformable) bean).toServer(field.getName(), value);
					if(valueToServer != null){
						value = valueToServer;
					}
				}
				String key = field.getName();
				params.put(key, value);
				field.set(bean, value);
				return value;
//			}
		}else{
			return null;
		}
	}

	@Override
	public boolean setContent(Context context, View view, Object bean, String name, Object value)
			throws FieldNotPublicException, TypeNotSupportedException {
		if(view instanceof TextView){
			if (bean instanceof Transformable) {
				Object valueFromServe = ((Transformable) bean).fromServer(name, value);
				if(valueFromServe != null){
					value = valueFromServe;
				}
			}
			String str = getString(value);
			((TextView) view).setText(str);
			return true;
		}else{
			return false;
		}
	}

}
