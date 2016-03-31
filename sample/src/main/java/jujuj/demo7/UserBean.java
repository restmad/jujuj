package jujuj.demo7;


import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import provider.database.Entity;

@Table(name = "UserBean")
class UserBean extends Entity implements Comparable<UserBean>{
	
	@Column(name = "portrait")
	public String userPortrait;

	@Column(name = "userName")
	public String userName;

	@Column(name = "userId")
	public int userId;

	@Column(name = "email")
	public String email;

	@Column(name = "married")
	public boolean married;

	public UserBean(){
		super();  
	}

	@Override
	public Entity query() {
		UserBean entity = new Select().from(UserBean.class).where("userId = ?", userId).executeSingle();
		return entity;
	}

	@Override
	public int compareTo(UserBean another) {
		return another.userId - userId;
	}
}
