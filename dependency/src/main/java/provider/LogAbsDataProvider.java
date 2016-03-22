package provider;

import android.util.Log;

import framework.provider.AbsDataProvider;
import framework.provider.Listener;

public abstract class LogAbsDataProvider extends AbsDataProvider {

    protected abstract String getLogTag();

    @SuppressWarnings("unchecked")
    protected void response(Listener.Response response, Object obj){
        if (obj == null){
            Log.d(getLogTag(), "get nothing");
        }else {
            Log.d(getLogTag(), obj.toString());
        }
        response.onResponse(obj);
    }
}
