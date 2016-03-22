package provider;

import android.content.Context;
import android.util.Log;
import android.util.LruCache;

import java.util.Map;

import framework.provider.Listener;

public class CacheProvider extends LogAbsDataProvider{

    private final String TAG = "CacheProvider";
    private LruCache<String, Object> mLruCache ;

    public CacheProvider(){
        mLruCache = new LruCache<>((int) (Runtime.getRuntime().totalMemory() / 20));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleData(Context context, String uri, Map<String, String> params, Class cls, Listener.Response response, Listener.Error error) {
        Object value = mLruCache.get(uri + params.toString());
        response(response, value);
        response.onResponse(value);
    }

    @Override
    public void handleResult(Context context, String uri, Map<String, String> params, Object result) {
        mLruCache.put(uri + params.toString(), result);
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }
}
