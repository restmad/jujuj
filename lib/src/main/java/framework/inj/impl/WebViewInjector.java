package framework.inj.impl;

import java.lang.reflect.Field;
import java.util.HashMap;

import framework.inj.entity.Transformable;
import framework.inj.exception.FieldNotPublicException;
import framework.inj.exception.TypeNotSupportedException;

import android.content.Context;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

/**
 * 
 * @author ss
 * supported type: Collection
 */
public class WebViewInjector extends ViewInjector{

	@Override
	public String addParams(View view, HashMap<String, String> params, Object bean, Field field)
			throws Exception{
		return null;
	}

	@Override
	public boolean setContent(Context context, View view, Object bean, Field field) 
			throws FieldNotPublicException, TypeNotSupportedException {
		if(view instanceof WebView){
			Object value = null;
			try {
				value = field.get(bean);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				throw new FieldNotPublicException("The field is not public. In class " +
						bean.getClass().getName() + ", field " + field.getName());
			}
			if (bean instanceof Transformable) {
				Object valueFromServer = ((Transformable) bean).fromServer(field.getName(), value);
				if(valueFromServer != null){
					value = valueFromServer;
				}
			}
			if (value instanceof String) {
				WebView web = (WebView) view;
				WebSettings settings = web.getSettings(); 
				settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); 
				web.loadData((String) value, "text/html; charset=UTF-8", null);
			}else{
				throw new TypeNotSupportedException("The type of the field is not a Collection. In class " +
						bean.getClass().getName() + ", field " + field.getName());
			}
			return true;
		}else{
			return false;
		}
	}

}
