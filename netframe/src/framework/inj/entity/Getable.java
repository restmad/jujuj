package framework.inj.entity;

import org.json.JSONObject;

import android.content.Context;

public interface Getable {

	public abstract int getSubmitButtonId();
	public abstract void onPostResponse(Context context, JSONObject obj);
	public abstract String onPostUrl(Context context);

}
