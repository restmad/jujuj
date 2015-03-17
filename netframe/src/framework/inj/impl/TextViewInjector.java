package framework.inj.impl;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * 
 * @author ss
 * supported type String
 */
public class TextViewInjector extends ViewInjector{

	@Override
	public boolean addParams(View view, HashMap<String, String> params, Object bean, Field field)
			throws Exception{
		if(view instanceof TextView){
//			if(view.getClass().isAssignableFrom(TextView.class)){
//				return false;
//			}else{
				String value = ((TextView) view).getText().toString();
				String key = field.getName();
				params.put(key, value);
				field.set(bean, value);
				return true;
//			}
		}else{
			return false;
		}
	}

	@Override
	public boolean setContent(Context context, View view, Object bean, Field field) 
			throws Exception{
		if(view instanceof TextView){
			Object value = field.get(bean);
			String str = getString(value);
			((TextView) view).setText(str);
			return true;
		}else{
			return false;
		}
	}

}
