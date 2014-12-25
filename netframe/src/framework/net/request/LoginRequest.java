package framework.net.request;

public class LoginRequest {

	public String userName;
	public String pwd;
	
	public LoginRequest(String userName, String pwd) {
		this.userName = userName;
		this.pwd = pwd;
	}
	
}
