package com.uni.netframe;

import com.activeandroid.ActiveAndroid;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.Map;

public class NetframeApplication extends com.activeandroid.app.Application{

    public static final String TAG = NetframeApplication.class
            .getSimpleName();
 
    private RequestQueue mRequestQueue;
 
    private static NetframeApplication mInstance;
 
    public static synchronized NetframeApplication getInstance() {
        return mInstance;
    }
 
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
 
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
 
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
 
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    
	@Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        requestQueue = Volley.newRequestQueue(this);
        mInstance = this;
    }

    private static final String SET_COOKIE_KEY = "set-cookie";
    private static final String COOKIE_KEY = "cookie";
    private static final String SESSION_COOKIE = "JSESSIONID";

    private RequestQueue requestQueue;
    private SharedPreferences preferences;

    /**
     * Checks the response headers for session cookie and saves it
     * if it finds it.
     * @param headers Response Headers.
     */
    public final void checkSessionCookie(Map<String, String> headers) {
        for(String header : headers.keySet()){
            if(header.toLowerCase().equals(SET_COOKIE_KEY)){
                if(headers.get(header).startsWith(SESSION_COOKIE)){
                    String cookie = headers.get(header);
                    if (cookie.length() > 0) {
//                        String[] splitCookie = cookie.split(";");
//                        String[] splitSessionId = splitCookie[0].split("=");
//                        cookie = splitSessionId[1];
                        SharedPreferences.Editor prefEditor = preferences.edit();
                        prefEditor.putString(SESSION_COOKIE, cookie);
                        prefEditor.commit();
                        Log.d(TAG, "getSessionCookie:"+cookie);
                    }
                }
            }
        }
//        if (headers.containsKey(SET_COOKIE_KEY)
//                && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
//
//        }
    }

    public void clearCookie(){
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString(SESSION_COOKIE, "");
        prefEditor.commit();
    }

    /**
     * Adds session cookie to headers if exists.
     * @param headers
     */
    public final void addSessionCookie(Map<String, String> headers) {
        String sessionId = preferences.getString(SESSION_COOKIE, "");
        if (sessionId.length() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(sessionId);
            if (headers.containsKey(COOKIE_KEY)) {
                builder.append("; ");
                builder.append(headers.get(COOKIE_KEY));
            }
            String cookie = builder.toString();
            headers.put(COOKIE_KEY, cookie);
            Log.d(TAG, "addSessionCookie:"+cookie);
        }
    }
}
