package framework.net.test;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import framework.net.bean.UserVo;
import framework.net.impl.NetworkRequest;
import framework.net.request.LoginRequest;


public class NetworkTest {
	
	private final String TAG = "LoginTest";
	
	public void getAllUsers(){

		/*
		 * get array from server
		 */
		NetworkRequest.requestArray(
				"all_user.php", 
				new Response.Listener<JSONArray>(){

					@Override
					public void onResponse(JSONArray obj) {
						Gson gson = new Gson();
						/*
						 * convert from JSON to user info
						 * NOTICE that the members' name in UserVo MUST be
						 * as same as they are in the database in the server
						 */
						ArrayList<UserVo> allUsers = gson.fromJson(obj.toString(),
								new TypeToken<ArrayList<UserVo>>() {}.getType());
						Log.d(TAG, "get all");
						for(UserVo user:allUsers){
							Log.d(TAG, user.id + "," + user.userName +","+ user.email +","+ user.userName);
						}
						
					}
			
				}, null);
	}

	public void login(final LoginRequest login){

		/*
		 * get object from server
		 */
		NetworkRequest.requestJson(
				//URL for signing in 
				"sign_in.php", 
				/*
				 * Sing in request LoginVo, which contains:
				 *  String userName;
				 *	String pwd;
				 */
				login, 
				//handle sign in result
				new Response.Listener<JSONObject>(){

					@Override
					public void onResponse(JSONObject obj) {
						Gson gson = new Gson();
						/*
						 * convert from JSON to user info
						 * NOTICE that the members' name in UserVo MUST be
						 * as same as they are in the database in the server
						 */
						UserVo user = gson.fromJson(obj.toString(), UserVo.class);
						Log.d(TAG, user.id + "," + user.userName +","+ user.email +","+ user.userName);
						
					}
			
				}, null);
	}
	
}
