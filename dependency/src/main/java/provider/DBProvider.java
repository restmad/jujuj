package provider;

import android.content.Context;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import framework.inj.entity.Listable;
import framework.provider.AbsDataProvider;
import framework.provider.Listener;

public class DBProvider extends AbsDataProvider {

    private final String TAG = "DBProvider";

    @SuppressWarnings("unchecked")
    @Override
    public void handleData(Context context, String uri, Map<String, String> params, Class cls, Listener.Response response, Listener.Error error) {

        if (cls == null){
            response.onResponse(null);
            return;
        }
        if(cls.isAssignableFrom(Entity.class)){
            Log.d(TAG, "handling....");
            Entity entity;
            try {
                entity = (Entity) cls.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                response.onResponse(null);
                return;
            }
            Entity target = entity.query();
            response.onResponse(target);
        } else if(cls.isAssignableFrom(Listable.class)){
            Type[] genType = cls.getGenericInterfaces();
            Type[] typeArguments = ((ParameterizedType) genType[0]).getActualTypeArguments();
            Class genericCls = (Class) typeArguments[0];
            if (genericCls.isAssignableFrom(Entity.class)){
                Collection collection = new Select().all().from(genericCls).execute();
                Listable listable;
                try {
                    listable = (Listable) cls.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    response.onResponse(null);
                    return;
                }
                if (listable.getList() != null){
                    listable.getList().addAll(collection);
                }
            }
        } else{
            Log.d(TAG, "passed....");
            response.onResponse(null);
        }
    }

    @Override
    public void handleResult(Context context, Object result) {
        if (result == null){
            return;
        }
         if (result instanceof Collection){
             Collection collection = (Collection) result;
             for (Object item : collection){
                 if (item instanceof Model){
                     ((Model)item).save();
                 }else {
                     break;
                 }
             }
         }else {
             if (result instanceof Model){
                 ((Model)result).save();
             }
         }
    }

}
