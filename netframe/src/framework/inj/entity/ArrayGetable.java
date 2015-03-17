package framework.inj.entity;

import org.json.JSONArray;

import android.content.Context;

public interface ArrayGetable extends Getable{

	public abstract void onPostResponse(Context context, JSONArray array);
}
