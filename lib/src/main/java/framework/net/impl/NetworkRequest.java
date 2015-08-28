package framework.net.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.uni.netframe.NetframeApplication;

public class NetworkRequest{
	
	private static Response.ErrorListener defaultError = new Response.ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError e) {
			e.printStackTrace();
			Log.d(TAG, "error:"+e.getMessage());
		}

	};

	public static void requestJson(String url, Map<String, String> params, String charset,
			Response.Listener<JSONObject> listener, Response.ErrorListener error) {

		JSONObjectRequest jsonObjReq = new JSONObjectRequest(url, params, charset, 
				listener, error == null ? defaultError : error);

		NetframeApplication.getInstance().addToRequestQueue(jsonObjReq);
	}

	public static void requestJson(String url, String charset,
			Response.Listener<JSONObject> listener, Response.ErrorListener error) {

		Type mySuperClass = listener.getClass().getGenericSuperclass();
		Type type = ((ParameterizedType)mySuperClass).getActualTypeArguments()[0];
		Log.d(TAG, type.toString());

		JSONObjectRequest jsonObjReq = new JSONObjectRequest(url, null, charset, listener, error == null ? defaultError : error);

		NetframeApplication.getInstance().addToRequestQueue(jsonObjReq);
	}

	private final static String TAG = "HttpRequest";
	/**
	 * 
	 * @param url
	 * @param bean
	 *            params
	 * @param listener
	 *            callback, either Response.Listener<JSONObject> or
	 *            Response.Listener<JSONArray>
	 * @param error
	 *            callback, use a default callback to print stack trace if null
	 *            is set
	 * 
	 */
	public static void requestJson(String url, final Object bean, String charset,
			Response.Listener<JSONObject> listener, Response.ErrorListener error) {

		Map<String, String> params = objToMap(bean);

		JSONObjectRequest jsonObjReq = new JSONObjectRequest(url, params, charset, 
				listener, error == null ? defaultError : error);

		NetframeApplication.getInstance().addToRequestQueue(jsonObjReq);
	}

    /**
     * god bless GSON performs better than reflection
     * @param obj
     * @return
     */
    private static Map<String, String> objToMap(Object obj){
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

	/**
	 * 
	 * @param params
	 *            params
	 * @param listener
	 *            callback, either Response.Listener<JSONObject> or
	 *            Response.Listener<JSONArray>
	 * @param error
	 *            callback
	 * 
	 */
	public static void requestArray(String url, Map<String, String> params, String charset,
			Response.Listener<JSONArray> listener, Response.ErrorListener error) {

		JSONArrayRequest jsonObjReq = new JSONArrayRequest(url, params, charset, 
				listener, error == null ? defaultError : error);

		NetframeApplication.getInstance().addToRequestQueue(jsonObjReq);
	}

	/**
	 * 
	 * @param charset
	 *            params
	 * @param listener
	 *            callback, either Response.Listener<JSONObject> or
	 *            Response.Listener<JSONArray>
	 * @param error
	 *            callback
	 * 
	 */
	public static void requestArray(String url, String charset,
			Response.Listener<JSONArray> listener, Response.ErrorListener error) {

		JSONArrayRequest jsonObjReq = new JSONArrayRequest(url, null, charset,
				listener, error == null ? defaultError : error);

		NetframeApplication.getInstance().addToRequestQueue(jsonObjReq);
	}

	/**
	 *
	 * @param url
	 * @param bean
	 *            params
	 * @param listener
	 *            callback, either Response.Listener<JSONObject> or
	 *            Response.Listener<JSONArray>
	 * @param error
	 *            callback, use a default callback to print stack trace if null
	 *            is set
	 *
	 */
	public static void requestArray(String url, final Object bean, String charset,
			Response.Listener<JSONArray> listener, Response.ErrorListener error) {

        Map<String, String> params = objToMap(bean);

		JSONArrayRequest jsonObjReq = new JSONArrayRequest(url, params, charset,
				listener, error == null ? defaultError : error);

		NetframeApplication.getInstance().addToRequestQueue(jsonObjReq);
	}

}