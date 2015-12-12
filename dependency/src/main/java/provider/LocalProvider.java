package provider;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import framework.provider.AbsDataProvider;
import framework.provider.Listener;

/**
 * Created by shinado on 15/8/28.
 */
public class LocalProvider extends AbsDataProvider {

    private final String TAG = "LocalProvider";

    @Override
    public void handleData(Context context, String uri, Map<String, String> params, Class cls, Listener.Response response, Listener.Error error) {
//        if (uri.endsWith("netframe_get_user.php")){
//            Log.d(TAG, "handling....");
//            String json = "{\"userPortrait\":\"http:\\/\\/img3.douban.com\\/icon\\/ul50757825-11.jpg\",\"userName\":\"Dan\",\"email\":\"fckgfw@china.com\",\"married\":\"false\",\"numbers\":[{\"number\":\"13555855443\"},{\"number\":\"15366783412\"}]}";
//
//            Gson gson = generateGson();
//            Object obj = gson.fromJson(json, cls);
//            response.onResponse(obj);
//        }else {
            Log.d(TAG, "passed....");
            response.onResponse(null);
//        }
    }

    private Gson generateGson(){
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss.S")
                .create();
    }
}
