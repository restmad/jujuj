package framework.inj.entity;

import org.json.JSONObject;

import com.activeandroid.Model;
import com.android.volley.Response;
import com.google.gson.Gson;

import framework.net.impl.NetworkRequest;

public abstract class Entity extends Model{

	public boolean isLoaded = false;
	
	public Entity(){
		super();
	}
	
	public void setForignKey(Entity entity){}

	public abstract Entity query();

}
