package framework.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import java.lang.reflect.Field;

public abstract class AbsPreference {

    private SharedPreferences mPref;

    public AbsPreference(Context context, String name){
        mPref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        getAllFields();
    }

    public void save(View view){
        SharedPreferences.Editor editor = mPref.edit();
        for (Field field : getClass().getDeclaredFields()) {
            String key = field.getName();
            Object value = null;
            try {
                value = field.get(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (value != null){
                if(value instanceof String){
                    editor.putString(key, (String) value);
                } else if(value instanceof Integer){
                    editor.putInt(key, (Integer) value);
                } else if(value instanceof Long){
                    editor.putLong(key, (Long) value);
                } else if(value instanceof Boolean){
                    editor.putBoolean(key, (Boolean) value);
                } else if(value instanceof Float){
                    editor.putFloat(key, (Float) value);
                }
            }
        }
        editor.commit();
    }

    private void getAllFields(){
        for (Field field : getClass().getDeclaredFields()) {
            String key = field.getName();
            Class cls = field.getClass();
            try {
                if (cls == String.class){
                    field.set(this, mPref.getString(key, ""));
                } else if (cls == Integer.class){
                    field.set(this, mPref.getInt(key, -1));
                } else if (cls == Float.class){
                    field.set(this, mPref.getFloat(key, -1f));
                } else if (cls == Boolean.class){
                    field.set(this, mPref.getBoolean(key, false));
                } else if (cls == Long.class){
                    field.set(this, mPref.getLong(key, -1l));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void refresh(){
        getAllFields();
    }

    public abstract int getSaveButtonId();

}
