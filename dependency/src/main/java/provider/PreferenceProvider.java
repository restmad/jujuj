package provider;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

import framework.provider.AbsDataProvider;
import framework.provider.Listener;

public class PreferenceProvider extends AbsDataProvider {

    public static final String URI_GET = "pref.get";
    public static final String URI_SAVE = "pref.save";

    @SuppressWarnings("unchecked")
    @Override
    public void handleData(Context context, String uri, Map<String, String> params, Class target, Listener.Response response, Listener.Error error) {
        switch (uri) {
            case URI_GET:
                Object obj = getItem(context, target);
                response.onResponse(obj);
                break;
            case URI_SAVE:
                save(context, params, target);
                response.onResponse(null);
                break;
            default:
                response.onResponse(null);
                break;
        }
    }

    @Override
    public void handleResult(Context context, Object result) {
        //TODO
    }

    //TODO not taking transforming into consideration
    private void save(Context context, Map<String, String> params, Class target){
        Object item;
        try {
            item = target.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        SharedPreferences sp = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        for (Field field : item.getClass().getDeclaredFields()){
            String key = field.getName();
            String val = params.get(key);
            Type type = field.getType();
            if (type == String.class){
                editor.putString(key, val);
            } else if (type == Integer.class){
                editor.putInt(key, Integer.parseInt(val));
            } else if (type == Float.class){
                editor.putFloat(key, Float.parseFloat(val));
            } else if (type == Boolean.class){
                editor.putBoolean(key, Boolean.parseBoolean(val));
            } else if (type == Long.class){
                editor.putLong(key, Long.parseLong(val));
            }
        }
        editor.apply();
    }

    private Object getItem(Context context, Class cls){
        SharedPreferences sp = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        Object item;
        try {
            item = cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        for (Field field : item.getClass().getDeclaredFields()) {
            String key = field.getName();
            Class fieldClass = field.getType();
            try {
                if (fieldClass == String.class){
                    field.set(item, sp.getString(key, ""));
                } else if (fieldClass == Integer.class){
                    field.set(item, sp.getInt(key, -1));
                } else if (fieldClass == Float.class){
                    field.set(item, sp.getFloat(key, -1f));
                } else if (fieldClass == Boolean.class){
                    field.set(item, sp.getBoolean(key, false));
                } else if (fieldClass == Long.class){
                    field.set(item, sp.getLong(key, -1L));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return item;
    }
}
