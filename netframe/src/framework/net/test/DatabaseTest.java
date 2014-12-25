package framework.net.test;

import java.util.List;

import android.util.Log;

import com.activeandroid.query.Select;

import framework.net.bean.UserVo;

public class DatabaseTest {

	private final String TAG = "DatabaseTest";
	
	public void runTest(){
		UserVo user1 = new UserVo(10, "Peter", "p@p.p", 1);
		UserVo user2 = new UserVo(11, "Jim", "j@p.k", 0);
		saveUser(user1);
		saveUser(user2);
		
		//update
		user1.email = "p@q.r";
		saveUser(user1);

		Log.d(TAG, "get all");
		List<UserVo> all = getAllUsers();
		for(UserVo user:all){
			Log.d(TAG, user.id + "," + user.userName +","+ user.email +","+ user.userName);
		}

	}

	public UserVo selectUserById(int id){
		return new Select().from(UserVo.class).where("iId = "+id).executeSingle();
	}

	public List<UserVo> getAllUsers(){
		return new Select().all().from(UserVo.class).execute();
	}

	public void saveUser(UserVo user){
		user.save();
	}

	public void deleteUser(UserVo user){
		user.delete();
	}
	
}
