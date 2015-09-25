package framework.inj.impl;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;

import framework.inj.exception.TypeNotSupportedException;
import framework.inj.groupview.Adaptable;
import framework.inj.groupview.LazyAdapter;
import framework.inj.groupview.Listable;
import framework.inj.groupview.ListableAdapter;

/**
 * 
 * @author ss
 * supported type: Collection
 */
public class AbsListViewInjector extends ViewInjector {

	@Override
	public String addParams(View view, HashMap<String, String> params, Object bean, Field field)
			throws Exception{
		return null;
	}

	@Override
	public boolean setContent(Context context, View view, Object bean, String name, Object value){
		if(view instanceof AdapterView){
			AdapterView adapterView = (AdapterView) view;
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
					adapterView.setAdapter(((Adaptable)bean).getAdapter(context));
				}else{
					LazyAdapter adp = new LazyAdapter(context, (Collection<Object>) value);
					adapterView.setAdapter(adp);
				}
			}else if(value instanceof Adaptable){
				adapterView.setAdapter(((Adaptable)value).getAdapter(context));
			}else if(value instanceof Listable) {
				adapterView.setAdapter(new ListableAdapter<>(context, (Listable) value));
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
