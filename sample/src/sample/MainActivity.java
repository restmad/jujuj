package sample;

import framework.inj.entity.MutableEntity;
import framework.inj.impl.Netframe;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		UserBean bean = new UserBean();
		bean.userName = "Dan";
		Netframe.inject(this, new MutableEntity<UserBean>(bean));
	}
	
}
