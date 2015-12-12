package provider;

import android.content.Context;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import framework.provider.AbsDataProvider;
import framework.provider.Listener;

public class CacheProvider extends AbsDataProvider{

    private final String TAG = "CacheProvider";
    private static CacheProvider instance;

    private CacheProvider(){
    }

    public static CacheProvider getInstance(){
        if(instance == null){
            instance = new CacheProvider();
        }
        return instance;
    }

    public static void destroy(){
        instance = null;
    }

    private Map<String, SoftReference<Object>> cache = new HashMap<>();

    @Override
    public void handleData(Context context, String uri, Map<String, String> params, Class cls, Listener.Response response, Listener.Error error) {
        if (cls == null){
            response.onResponse(null);
        }
        Log.d(TAG, "handling....");
        String key = cls.getName() + params.toString();
        SoftReference<Object> ref = cache.get(key);
        if(ref != null){
            Log.d(TAG, "get");
            response.onResponse(ref.get());
        }else{
            Log.d(TAG, "passed...");
            response.onResponse(null);
        }
    }

    public void put(Map<String, String> params, Class cls, Object target){
        String key = cls.getName() + params.toString();
        SoftReference<Object> ref = cache.get(key);
        if(ref == null){
            Log.d(TAG, "put item");
            cache.put(key, new SoftReference(target));
        }
    }
}
