package provider;

import android.content.Context;
import android.util.Log;

import java.util.Map;

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
            CacheProvider.getInstance().put(params, cls, target);
            response.onResponse(target);
        }else{
            Log.d(TAG, "passed....");
            response.onResponse(null);
        }
    }

}
