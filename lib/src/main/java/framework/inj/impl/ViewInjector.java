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
			Object value = method.invoke(bean);
			value = transform(bean, value, method.getName());
			return setContent(context, view, bean, method.getName(), value);
		} catch (IllegalAccessException e) {
			throw new FieldNotPublicException("The method is not public. In class " +
					bean.getClass().getName() + ", method " + method.getName());
		} catch (InvocationTargetException e) {
			//TODO
			e.printStackTrace();
		}
		return false;
	}

	public boolean setContent(Context context, View view, Object bean, Field field){
		try {
			Object value = field.get(bean);
			return setContent(context, view, bean, field.getName(), value);
		} catch (IllegalArgumentException e) {
			//TODO
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			throw new FieldNotPublicException("The field is not public. In class " +
					bean.getClass().getName() + ", field " + field.getName());
		}
		return false;
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

	public abstract boolean setContent(Context context, View view, Object bean, String name, Object value);
}
