package framework.inj.impl;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.Collection;
import java.util.HashMap;

import framework.inj.exception.TypeNotSupportedException;
import framework.inj.groupview.AbsList;
import framework.inj.groupview.Adaptable;
import framework.inj.groupview.LazyAdapter;
import framework.inj.groupview.ListableAdapter;

/**
 * 
 * @author ss
 * supported type: Collection
 */
public class AbsListViewBinder extends ViewBinder {

	@Override
	public String addParams(View view, HashMap<String, String> params, Object bean, String fieldName, String packageName)
			throws Exception{
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean setContent(Context context, View view, Object bean, String name, Object value, String packageName){
		if(view instanceof AdapterView){
			AdapterView adapterView = (AdapterView) view;
			if (value instanceof Collection) {
				Log.d("HttpRequest", "ListView setContent");
				if (bean instanceof Adaptable){
					adapterView.setAdapter(((Adaptable)bean).getAdapter(context));
				}else{
					LazyAdapter adp = new LazyAdapter(context, (Collection<Object>) value, packageName);
					adapterView.setAdapter(adp);
				}
			}else if(value instanceof Adaptable){
				adapterView.setAdapter(((Adaptable)value).getAdapter(context));
			}else if(value instanceof AbsList) {
				adapterView.setAdapter(new ListableAdapter(context, (AbsList) value, packageName));
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
