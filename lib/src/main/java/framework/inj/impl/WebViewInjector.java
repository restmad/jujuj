package framework.inj.impl;

import android.content.Context;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import java.lang.reflect.Field;
import java.util.HashMap;

import framework.inj.exception.TypeNotSupportedException;

/**
 * 
 * @author ss
 * supported type: Collection
 */
public class WebViewInjector extends ViewInjector {

	@Override
	public String addParams(View view, HashMap<String, String> params, Object bean, String fieldName)
			throws Exception{
		return null;
	}


	@Override
	public boolean setContent(Context context, View view, Object bean, String name, Object value){
		if(view instanceof WebView){

			if (value instanceof String) {
				WebView web = (WebView) view;
				WebSettings settings = web.getSettings(); 
				settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); 
				web.loadData((String) value, "text/html; charset=UTF-8", null);
			}else{
				throw new TypeNotSupportedException("The type of the field is not a Collection. In class " +
						bean.getClass().getName() + ", field " + name);
			}
			return true;
		}else{
			return false;
		}
	}

}
