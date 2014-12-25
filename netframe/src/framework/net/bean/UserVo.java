package framework.net.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "User")
public class UserVo extends Model {
	
	/**
	 * avoid using id for column names
	 * index = true for primary key 
	 */
	@Column(name = "iId", index = true)
	public long id;
	@Column(name = "userName")
	public String userName;
	@Column(name = "email")
	public String email;
	@Column(name = "gender")
	public int gender;
	
	public UserVo(){ 
		/*
		 * super must be called
		 */
		super();
	}
	
	public UserVo(int id, String userName, String email, int gender) {
		//super must be called
		super();
		this.id = id;
		this.userName = userName;
		this.email = email;
		this.gender = gender;
	}
	
	
}
