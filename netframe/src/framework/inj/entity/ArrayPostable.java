package framework.inj.entity;

import org.json.JSONArray;

import android.content.Context;

public interface ArrayPostable extends Postable{

	public abstract void onPostResponse(Context context, JSONArray array);
}
