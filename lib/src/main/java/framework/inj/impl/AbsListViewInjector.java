package framework.inj.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.activeandroid.util.Log;

import framework.inj.entity.Transformable;
import framework.inj.exception.FieldNotPublicException;
import framework.inj.exception.TypeNotSupportedException;
import framework.inj.groupview.Adaptable;
import framework.inj.groupview.LazyAdapter;
import android.R;
import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;

/**
 * 
 * @author ss
 * supported type: Collection
 */
public class AbsListViewInjector extends ViewInjector{

	@Override
	public String addParams(View view, HashMap<String, String> params, Object bean, Field field)
			throws Exception{
		return null;
	}

	@Override
	public boolean setContent(Context context, View view, Object bean, Field field) 
			throws FieldNotPublicException, TypeNotSupportedException {
		if(view instanceof AbsListView){
			AbsListView listView = (AbsListView) view;
			Object value = getValue(bean, field);
			if (value instanceof Collection) {
//				ListAdapter adapter = listView.getAdapter();
//				if(adapter != null){
//					if(adapter instanceof LazyAdapter){
//						((LazyAdapter)adapter).setData((Collection<Object>) value);
//						return true;
//					}
//				}
				
				Log.d("HttpRequest", "ListView setContent");
				if (bean instanceof Adaptable){
					listView.setAdapter(((Adaptable)bean).getAdapter(context));
				}else{
					LazyAdapter adp = new LazyAdapter(context, (Collection<Object>) value);
					listView.setAdapter(adp);
				}
			}else{
				throw new TypeNotSupportedException("The type of the field is not a Collection. In class " +
						bean.getClass().getName() + ", field " + field.getName());
			}
			return true;
		}else{
			return false;
		}
	}
	
	private Object getValue(Object bean, Field field) throws FieldNotPublicException{
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
		return value;
	}

}
