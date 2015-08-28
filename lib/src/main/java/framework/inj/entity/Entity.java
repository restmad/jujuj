package framework.inj.entity;

import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.android.volley.Response;
import com.google.gson.Gson;

import java.util.List;

import framework.net.impl.NetworkRequest;

public abstract class Entity extends Model{

	public boolean isLoaded = false;
	
	public Entity(){
		super();
	}
	
	public void setForignKey(Entity entity){}

	public abstract Entity query();

	protected final <E extends Model> List<E> getMani(Class<? extends Model> type, String foreignKey) {
		return (new Select()).from(type).where(foreignKey + "=?", new Object[]{this.getId()}).execute();
	}
}
