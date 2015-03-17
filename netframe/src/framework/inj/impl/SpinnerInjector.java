package framework.inj.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import framework.inj.entity.Injectable;
import framework.inj.entity.MutableEntity;
import framework.inj.exception.TypeNotSupportedException;
import android.R;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * 
 * @author ss
 * supported type Map, String
 */
public class SpinnerInjector extends ViewInjector{

	@Override
	public boolean addParams(View view, HashMap<String, String> params, Object bean, Field field)
			throws Exception{
		if(view instanceof Spinner){
			Spinner spinner = (Spinner) view;
			String key = spinner.getSelectedItem().toString();
			Object mapValue = field.get(bean);
			if (bean instanceof Injectable) {
				mapValue = ((Injectable) bean).getObject();
			}
			if(mapValue instanceof Map){
				Map<String, String> map = (Map<String, String>) mapValue;
				String value = map.get(key);
				params.put(field.getName(), value+"");
			}else{
				throw new TypeNotSupportedException("The type of the field is not a Map. In class" +
						bean.getClass().getName() + ", field " + field.getName());
			}
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean setContent(Context context, View view, Object bean, Field field) 
			throws Exception{
		if(view instanceof Spinner){
			Object value = field.get(bean);
			if (bean instanceof Injectable) {
				value = ((Injectable) bean).getObject();
			}
			if(value instanceof Map){
				Map<String, String> map = (Map<String, String>) value;
				Spinner spinner = (Spinner) view;
				java.util.Set<String> keySet = map.keySet();
				String[] items = new String[keySet.size()];
				for(int i=0; i<keySet.size(); i++){
					String key = (String) keySet.toArray()[i];
					items[i] = key;
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						context, R.layout.simple_spinner_item, items);
				spinner.setAdapter(adapter);
			}else{
				throw new TypeNotSupportedException("The type of the field is not a Map. In class" +
						bean.getClass().getName() + ", field " + field.getName());
			}
			return true;
		}else{
			return false;
		}
	}

}
