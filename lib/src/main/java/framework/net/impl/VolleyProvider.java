package framework.net.impl;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.uni.netframe.NetframeApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import framework.net.abs.AbsDataProvider;
import framework.net.abs.Listener;

/**
 * Created by shinado on 15/8/27.
 */
public class VolleyProvider extends AbsDataProvider {

    private final String TAG = "HttpRequest";
    private Response.ErrorListener defaultError = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError e) {
            e.printStackTrace();
            Log.d(TAG, "error:" + e.getMessage());
        }

    };

    /**
     * god bless GSON performs better than reflection
     * @param obj
     * @return
     */
    private Map<String, String> objToMap(Object obj){
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            String str = new Gson().toJson(obj);
            JSONObject json = new JSONObject(str);
            Iterator<String> it = json.keys();
            while(it.hasNext()){
                String key = it.next();
                Object value = json.get(key);
                if(value instanceof Integer ||
                        value instanceof Long || value instanceof Double ||
                        value instanceof Float || value instanceof String){
                    map.put(key, value+"");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public void requestJson(String url, Object bean, String charset, final Listener.Response response, final Listener.Error error) {

        Map<String, String> params = objToMap(bean);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject json) {
                response.onResponse(json);
            }
        };
        JSONObjectRequest jsonObjReq = new JSONObjectRequest(url, params, charset,
                listener, error == null ? defaultError : new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                error.onError(volleyError.getMessage());
            }
        });

        NetframeApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public void requestArray(String url, Object bean, String charset, final Listener.Response response, final Listener.Error error) {

        Map<String, String> params = objToMap(bean);

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>(){

            @Override
            public void onResponse(JSONArray array) {
                response.onResponse(array);
            }
        };
        JSONArrayRequest jsonObjReq = new JSONArrayRequest(url, params, charset,
                listener, error == null ? defaultError : new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                error.onError(volleyError.getMessage());
            }
        });

        NetframeApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public int index() {
        return 0;
    }
}
