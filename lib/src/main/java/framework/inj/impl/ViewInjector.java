package framework.inj.impl;

import java.lang.reflect.Field;
import java.util.HashMap;

import framework.core.Jujuj;
import framework.inj.exception.TypeNotSupportedException;
import android.content.Context;
import android.view.View;

public abstract class ViewInjector {

	protected String getString(Object value){
		if(value == null){
			return "";
		}
		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof Integer || value instanceof Long
				|| value instanceof Double || value instanceof Float) {
			return value + "";
		}
		else{
			throw new TypeNotSupportedException("The type of the field is not an Integer, Long, Double, Float or String");
		}

	}

	/**
	 * 
	 * @param view
	 * @param params
	 * @param bean
	 * @param field
	 * @return the value of this field, null if not his obligation
	 * @throws Exception
	 */
	public abstract String addParams(View view, HashMap<String, String> params, Object bean, Field field)
			throws Exception;
	
	public abstract boolean setContent(Context context, View view, Object bean, Field field);
}
