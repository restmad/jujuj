package demo2;

import com.activeandroid.query.Select;
import com.uni.netframe.Netframe;

import framework.inj.entity.MutableEntity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class Demo2Activity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		UserBean bean = new UserBean();
		bean.userName = "Dan";

		Netframe.getInstance().inject(this, new MutableEntity(bean));
	}
	
}
