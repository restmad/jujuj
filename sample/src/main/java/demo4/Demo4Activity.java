package demo4;

import android.app.Activity;
import android.os.Bundle;

import com.uni.netframe.Configuration;
import com.uni.netframe.Netframe;

import demo2.UserBean;
import framework.inj.entity.MutableEntity;

/**
 * use a same layout and data as demo2,
 * which provided by a local provider
 */
public class Demo4Activity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		UserBean bean = new UserBean();
		bean.userName = "Dan";

		Netframe jujuj = Netframe.getInstance();
		jujuj.init(new Configuration.Builder().setNetworkRequest(new LocalProvider()).build());
		jujuj.inject(this, new MutableEntity(bean));
	}
	
}
