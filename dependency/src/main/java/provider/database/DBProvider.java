package provider.database;

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
import provider.LogAbsDataProvider;

public class DBProvider extends LogAbsDataProvider {

    private final String TAG = "DBProvider";
    private AbsDBHandler mHandler;

    public void setDBHandler(AbsDBHandler handler){
        this.mHandler = handler;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleData(Context context, String uri, Map<String, String> params, Class cls, Listener.Response response, Listener.Error error) {
        if (cls == null){
            response.onResponse(null);
            return;
        }
        if (mHandler != null){
            Object obj = mHandler.query(params, uri);
            response(response, obj);
            return;
        }

        if(cls.isAssignableFrom(Entity.class)){
            Log.d(TAG, "handling....");
            Entity entity;
            try {
                entity = (Entity) cls.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                response(response, null);
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
                    response(response, null);
                    return;
                }
                if (listable.getList() != null){
                    listable.getList().addAll(collection);
                }
                response(response, listable);
            }
        } else{
            response(response, null);
        }
    }

    @Override
    public void handleResult(Context context, String uri, Map<String, String> params, Object result) {
        if (result == null){
            return;
        }
        Collection collection;
        if (result instanceof Collection || result instanceof Listable){
            if (result instanceof Collection){
                collection = (Collection) result;
            }else {
                collection = ((Listable)result).getList();
            }
            if (collection != null){
                for (Object item : collection){
                    if (item instanceof Model){
                        ((Model)item).save();
                    }else {
                        break;
                    }
                }
            }
        }else {

            if (result instanceof Model){
                ((Model)result).save();
            }
        }
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }
}
