package framework.inj.impl;

import android.content.Context;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import framework.inj.entity.utility.Transformable;
import framework.inj.exception.FieldNotPublicException;
import framework.inj.exception.TypeNotSupportedException;
import framework.util.L;

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
	 * return itself if it's not a Transformable
	 */
	private Object transform(Object bean, Object value, String name){
		if (bean instanceof Transformable) {
			Object valueFromServer = ((Transformable) bean).fromServer(name, value);
			if(valueFromServer != null){
				value = valueFromServer;
			}
		}
		return value;
	}

	public boolean setContent(Context context, View view, Object bean, Method method){
		try {
			String name = method.getName();
			Object value = method.invoke(bean);
			value = transform(bean, value, name);
			if(value == null){
				L.w("The value from method " + name + " is null. Are you sure that's what you really want?");
			}
			return setContent(context, view, bean, method.getName(), value);
		} catch (IllegalAccessException e) {
			throw new FieldNotPublicException("The method is not public. In class " +
					bean.getClass().getName() + ", method " + method.getName());
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public boolean setViewContent(Context context, View view, Object bean, String fieldName, Object value){
			value = transform(bean, value, fieldName);
			if(value == null){
				L.w("The value from field " + fieldName + " is null. Are you sure that's what you really want?");
			}
			return setContent(context, view, bean, fieldName, value);
	}

	/**
	 * 
	 * @param view
	 * @param params
	 * @param bean
	 * @param fieldName
	 * @return the value of this field, null if not his obligation
	 * @throws Exception
	 */
	public abstract String addParams(View view, HashMap<String, String> params, Object bean, String fieldName)
			throws Exception;

	public abstract boolean setContent(Context context, View view, Object bean, String name, Object value);
}
