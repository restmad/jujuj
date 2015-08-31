package provider.volley;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import framework.inj.entity.Downloadable;
import framework.provider.AbsDataProvider;
import framework.provider.Listener;
import jujuj.shinado.com.dependency.DefaultApplication;

/**
 * Created by shinado on 15/8/27.
 */
public class VolleyProvider extends AbsDataProvider {

    private final String TAG = "VolleyProvider";
    private Response.ErrorListener defaultError = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError e) {
            e.printStackTrace();
            Log.d(TAG, "error:" + e.getMessage());
        }

    };

    @Override
    public void handleData(String uri, Map<String, String> params, final Object target,
                           final Listener.Response response, final Listener.Error error) {
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject json) {

                Gson gson = generateGson();
                Class beanCls = target.getClass();
                Downloadable obj = (Downloadable) gson.fromJson(json.toString(), beanCls);

                response.onResponse(obj);
            }
        };

        JSONObjectRequest jsonObjReq = new JSONObjectRequest(uri, params, "utf-8",
                listener, error == null ? defaultError : new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                error.onError(volleyError.getMessage());
            }
        });

        DefaultApplication.getInstance().addToRequestQueue(jsonObjReq);

    }

    private  Gson generateGson(){
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss.S")
                .create();
    }

}
