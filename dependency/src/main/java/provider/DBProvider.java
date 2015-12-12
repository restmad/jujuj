package provider;

import android.content.Context;
import android.util.Log;

import java.util.Map;

import framework.provider.AbsDataProvider;
import framework.provider.Listener;

/**
 * Created by shinado on 15/8/31.
 */
public class DBProvider extends AbsDataProvider {

    private final String TAG = "DBProvider";

    @Override
    public void handleData(Context context, String uri, Map<String, String> params, Class cls, Listener.Response response, Listener.Error error) {
        if (cls == null){
            response.onResponse(null);
        }
        if(cls.isAssignableFrom(Entity.class)){
            Log.d(TAG, "handling....");
            Entity entity = null;
            try {
                entity = (Entity) cls.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
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
