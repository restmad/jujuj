package jujuj.demo5;

import java.io.Serializable;

public class UserBean implements Serializable{

	public UserBean(String userName){
		this.userName = userName;
	}

	public String userPortrait;

	public String userName;

	public String email;

	public boolean married;

}
