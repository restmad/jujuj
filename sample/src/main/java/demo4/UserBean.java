package demo4;

import android.content.Context;

import com.shinado.netframe.sample.R;

import java.util.ArrayList;
import java.util.HashMap;

import framework.inj.ViewInj;
import framework.inj.entity.Downloadable;
import sample.MyApplication;

public class UserBean {

	public UserBean(String userName){
		this.userName = userName;
	}

	public String userPortrait;

	public String userName;

	public String email;

	public boolean married;

	public ArrayList<Numbers> numbers;
}
