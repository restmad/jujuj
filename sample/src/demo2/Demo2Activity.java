package demo2;

import com.uni.netframe.Netframe;

import framework.inj.entity.MutableEntity;
import android.app.Activity;
import android.os.Bundle;

public class Demo2Activity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		UserBean bean = new UserBean();
		bean.userName = "Dan";
		Netframe.inject(this, new MutableEntity<UserBean>(bean));
	}
	
}
