package jujuj.dependent.entity;

import java.io.Serializable;

public class UserBean implements Serializable{

	public UserBean(int userId){
		this.userId = userId;
	}

	public int userId;

	public String userPortrait;

	public String userName;

}
