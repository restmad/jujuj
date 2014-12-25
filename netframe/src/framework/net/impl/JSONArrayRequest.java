package framework.net.impl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONArray;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class JSONArrayRequest extends Request<JSONArray> {

	private Listener<JSONArray> listener;
	private Map<String, String> params;

	public JSONArrayRequest(String url, Map<String, String> params,
			Listener<JSONArray> reponseListener, ErrorListener errorListener) {
		super(Method.POST, url, errorListener);
		this.listener = reponseListener;
		this.params = params;
	}

	public JSONArrayRequest(int method, String url, Map<String, String> params,
			Listener<JSONArray> reponseListener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.listener = reponseListener;
		this.params = params;
	}

	protected Map<String, String> getParams()
			throws com.android.volley.AuthFailureError {
		return params;
	};

	private final String TAG = "HttpRequest";

	@Override
	protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			Log.d(TAG, jsonString);
			return Response.success(new JSONArray(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	/**
	 * Passing some request headers
	 * */
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		return headers;
	}

	@Override
	public String getBodyContentType() {
		return "application/json; charset=utf-8";
	}

	@Override
	protected void deliverResponse(JSONArray response) {
		// TODO Auto-generated method stub
		listener.onResponse(response);
	}
}