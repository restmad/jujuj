package framework.inj.impl;

import java.lang.reflect.Field;
import java.util.HashMap;

import framework.inj.entity.Injectable;
import framework.inj.exception.TypeNotSupportedException;
import android.content.Context;
import android.view.View;

public abstract class ViewInjector {

	protected String getString(Object value) throws TypeNotSupportedException {

		if(value instanceof Injectable){
			value = ((Injectable) value).getObject();
		}
		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof Integer || value instanceof Long
				|| value instanceof Double || value instanceof Float) {
			return value + "";
		} else{
			throw new TypeNotSupportedException("The type of the field is not an Integer, Long, Double, Float or String");
		}

	}

	public abstract boolean addParams(View view, HashMap<String, String> params, Object bean, Field field)
			throws Exception;
	
	public abstract boolean setContent(Context context, View view, Object bean, Field field)
			throws Exception;
}
