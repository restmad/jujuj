package framework.inj.impl;

import java.lang.reflect.Field;
import java.util.HashMap;

import framework.inj.entity.Transformable;
import framework.inj.exception.FieldNotPublicException;
import framework.inj.exception.TypeNotSupportedException;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

/**
 * 
 * @author ss
 * supported type: Integer, String, Boolean
 */
public class CheckBoxInjector extends ViewInjector {

	@Override
	public String addParams(View view, HashMap<String, String> params,
			Object bean, Field field) throws Exception {
		if (view instanceof CheckBox) {
			String key = field.getName(); 
			boolean isChecked = ((CheckBox) view).isChecked();
			Object value = isChecked;
			if (bean instanceof Transformable) {
				//get value for model
				Object valueToServer = ((Transformable) bean).toServer(field.getName(), isChecked);
				if(valueToServer != null){
					value = valueToServer;
				}
			}
			params.put(key, value+"");
			field.set(bean, value);
			return value+"";
		} else {
			return null;
		}
	}

	@Override
	public boolean setContent(Context context, View view, Object bean, Field field)
			throws FieldNotPublicException, TypeNotSupportedException {
		if (view instanceof CheckBox) {
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
				Object valueFromServe = ((Transformable) bean).fromServer(field.getName(), value);
				if(valueFromServe != null){
					value = valueFromServe;
				}
			}
			if (value instanceof Boolean) {
				((CheckBox) view).setChecked((Boolean) value);
			} else {
				throw new TypeNotSupportedException("The type of the field is neither an Integer nor Boolean. In class " +
						bean.getClass().getName() + ", field " + field.getName());
			}
			return true;
		} else {
			return false;
		}
	}
	
}
