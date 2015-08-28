package framework.inj.entity;

import org.json.JSONObject;

import android.content.Context;

public interface Getable {

	public int getSubmitButtonId();
	public void onPostResponse(Context context, JSONObject obj);
	public String onPostUrl(Context context);
    public void onError(Context context, String msg);

}
