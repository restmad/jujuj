package jujuj.downloadable;

import android.app.Activity;
import android.os.Bundle;

import framework.core.Jujuj;
import framework.inj.entity.MutableEntity;

//test for downloadable
public class Demo2Activity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		UserBean bean = new UserBean();
		bean.userName = "Dan";

		Jujuj.getInstance().inject(this, new MutableEntity<>(bean));
	}
	
}
