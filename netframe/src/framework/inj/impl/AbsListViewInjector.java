package framework.inj.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import framework.inj.entity.Injectable;
import framework.inj.exception.TypeNotSupportedException;
import framework.inj.groupview.Adaptable;
import framework.inj.groupview.LazyAdapter;
import android.R;
import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * 
 * @author ss
 * supported type: Collection
 */
public class AbsListViewInjector extends ViewInjector{

	@Override
	public boolean addParams(View view, HashMap<String, String> params, Object bean, Field field)
			throws Exception{
		return false;
	}

	@Override
	public boolean setContent(Context context, View view, Object bean, Field field) 
			throws Exception{
		if(view instanceof AbsListView){
			Object value = field.get(bean);
			if (bean instanceof Injectable) {
				value = ((Injectable) bean).getObject();
			}
			if (value instanceof Collection) {
				if (bean instanceof Adaptable){
					((AbsListView) view).setAdapter(((Adaptable)bean).getAdapter(context));
				}else{
					LazyAdapter adapter = new LazyAdapter(context, (Collection<Object>) value);
					((AbsListView) view).setAdapter(adapter);
				}
			}else{
				throw new TypeNotSupportedException("The type of the field is not a Collection. In class" +
						bean.getClass().getName() + ", field " + field.getName());
			}
			return true;
		}else{
			return false;
		}
	}

}
