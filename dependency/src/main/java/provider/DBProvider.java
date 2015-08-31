package provider;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import framework.provider.AbsDataProvider;
import framework.provider.Listener;

/**
 * Created by shinado on 15/8/31.
 */
public class DBProvider extends AbsDataProvider {

    private final String TAG = "DBProvider";

    @Override
    public void handleData(String uri, Map<String, String> params, Object target, Listener.Response response, Listener.Error error) {
        if(target == null){
            Log.d(TAG, "passed....");
            response.onResponse(null);
            return;
        }

        if(target instanceof Entity){
            Log.d(TAG, "handling....");
            response.onResponse(((Entity) target).query());
        }else{
            Log.d(TAG, "passed....");
            response.onResponse(null);
        }
    }

}
