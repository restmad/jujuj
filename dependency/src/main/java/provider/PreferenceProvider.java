package provider;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Field;
import java.util.Map;

import framework.provider.AbsDataProvider;
import framework.provider.Listener;

public class PreferenceProvider extends AbsDataProvider {

    @Override
    public void handleData(Context context, String uri, Map<String, String> params, Class cls, Listener.Response response, Listener.Error error) {
        if (uri.equals("pref.get")){
            Object obj = getItem(context, cls);
            response.onResponse(obj);
        } else if(uri.equals("pref.save")){
            save(context, params);
            response.onResponse(null);
        }
    }

    public void save(Context context, Map<String, String> params){
        SharedPreferences sp = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for (String key : params.keySet()){
            String val = params.get(key);
            try {
                Integer iVal = Integer.parseInt(val);
                editor.putInt(key, iVal);
                continue;
            }catch (NumberFormatException ignored){
            }
            try {
                Float fVal = Float.parseFloat(val);
                editor.putFloat(key, fVal);
                continue;
            }catch (NumberFormatException ignored){
            }
            try {
                Long lVal = Long.parseLong(val);
                editor.putLong(key, lVal);
                continue;
            } catch (NumberFormatException ignored) {
            }
            editor.putString(key, val);
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
                    field.set(item, sp.getLong(key, -1l));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return item;
    }
}
