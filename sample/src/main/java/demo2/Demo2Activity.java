package demo2;

import com.activeandroid.query.Select;

import framework.core.Jujuj;
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

		Jujuj.getInstance().inject(this, new MutableEntity(bean));
	}
	
}
