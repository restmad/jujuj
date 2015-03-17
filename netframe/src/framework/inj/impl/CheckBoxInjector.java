package framework.inj.impl;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;

import framework.inj.entity.Injectable;
import framework.inj.exception.TypeNotSupportedException;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * 
 * @author ss
 * supported type: Integer, String, Boolean
 */
public class CheckBoxInjector extends ViewInjector {

	@Override
	public boolean addParams(View view, HashMap<String, String> params,
			Object bean, Field field) throws Exception {
		if (view instanceof CheckBox) {
			Object rType = field.get(bean);
			if (bean instanceof Injectable) {
				rType = ((Injectable) bean).getObject();
			}
			if(rType instanceof Integer ||
					rType instanceof String){
				if(transfer != null){
					boolean bValue = ((CheckBox) view).isChecked();
					String key = field.getName();
					int value = transfer.transferValue(bValue);
					params.put(key, value+"");
					if(rType instanceof Integer){
						field.set(bean, value);
					}else{
						field.set(bean, ""+value);
					}
				}
			}else if(rType instanceof Boolean){
				boolean value = ((CheckBox) view).isChecked();
				String key = field.getName();
				params.put(key, value+"");
				field.set(bean, value);
			}else{
				throw new TypeNotSupportedException("The type of the field is not an Integer, String or Boolean. In class" +
						bean.getClass().getName() + ", field " + field.getName());
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean setContent(Context context, View view, Object bean,
			Field field) throws Exception {
		Object value = field.get(bean);
		if (view instanceof CheckBox) {
			if (bean instanceof Injectable) {
				value = ((Injectable) bean).getObject();
			}
			if (value instanceof Boolean) {
				((CheckBox) view).setChecked((Boolean) value);
			} else if (value instanceof Integer) {
				if (transfer != null) {
					boolean b = transfer.transferValue((Integer) value);
					((CheckBox) view).setChecked(b);
				}
			} else {
				throw new TypeNotSupportedException("The type of the field is neither an Integer nor Boolean. In class" +
						bean.getClass().getName() + ", field " + field.getName());
			}
			return true;
		} else {
			return false;
		}
	}
	
	private Transfer transfer;
	
	public CheckBoxInjector setTransfer(Transfer transfer){
		this.transfer = transfer;
		return this;
	}

	public interface Transfer {

		public boolean transferValue(int i);

		public int transferValue(boolean b);

	}

}
