package provider.volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import framework.provider.AbsDataProvider;
import framework.provider.Listener;
import jujuj.shinado.com.dependency.DefaultApplication;

public class VolleyProvider extends AbsDataProvider {

    private final String TAG = "VolleyProvider";
    private Response.ErrorListener defaultError = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError e) {
            e.printStackTrace();
            Log.d(TAG, "error:" + e.getMessage());
        }

    };

    @SuppressWarnings("unchecked")
    @Override
    public void handleData(Context context, String uri,
                           final Map<String, String> params, final Class cls,
                           final Listener.Response response, final Listener.Error error) {
        if (!uri.startsWith("http://")){
            response.onResponse(null);
            return;
        }

        Response.Listener listener;
        Request request;

        String name = cls.getName();
        if (name.endsWith("ParameterizedTypeImpl")){
            listener = new Response.Listener<JSONArray>(){

                @Override
                public void onResponse(JSONArray array) {
                    Log.d(TAG, array.toString());

                    Gson gson = generateGson();
                    Object obj = gson.fromJson(array.toString(), cls);

                    response.onResponse(obj);
                }
            };

            request = new JSONArrayRequest(uri, params, "utf-8",
                    listener, error == null ? defaultError : new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    error.onError(volleyError.getMessage());
                }
            });
        }else{
            listener = new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject json) {
                    Log.d(TAG, json.toString());

                    Gson gson = generateGson();
                    Object obj = gson.fromJson(json.toString(), cls);

                    response.onResponse(obj);
                }
            };

            request = new JSONObjectRequest(uri, params, "utf-8",
                    listener, error == null ? defaultError : new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    error.onError(volleyError.getMessage());
                }
            });
        }

        DefaultApplication.getInstance().addToRequestQueue(request);

    }

    @Override
    public void handleResult(Context context, Object result) {
        //boss doesn't need to handle this
    }

    private  Gson generateGson(){
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss.S")
                .create();
    }

}
