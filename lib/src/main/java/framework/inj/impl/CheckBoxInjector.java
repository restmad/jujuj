package framework.inj.impl;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import java.lang.reflect.Field;
import java.util.HashMap;

import framework.inj.entity.utility.Transformable;
import framework.inj.exception.TypeNotSupportedException;

/**
 * 
 * @author ss
 * supported type: Integer, String, Boolean
 */
public class CheckBoxInjector extends ViewInjector {

	@Override
	public String addParams(View view, HashMap<String, String> params,
			Object bean, String  fieldName) throws Exception {
		if (view instanceof CheckBox) {
			boolean isChecked = ((CheckBox) view).isChecked();
			Object value = isChecked;
			if (bean instanceof Transformable) {
				//get value for model
				Object valueToServer = ((Transformable) bean).toServer(fieldName, isChecked);
				if(valueToServer != null){
					value = valueToServer;
				}
			}
			params.put(fieldName, value+"");
			return value+"";
		} else {
			return null;
		}
	}


	@Override
	public boolean setContent(Context context, View view, Object bean, String name, Object value){
		if (view instanceof CheckBox) {

			if (value instanceof Boolean) {
				((CheckBox) view).setChecked((Boolean) value);
			} else {
				throw new TypeNotSupportedException("The type of the field is neither an Integer nor Boolean. In class " +
						bean.getClass().getName() + ", field " + name);
			}
			return true;
		} else {
			return false;
		}
	}
	
}
