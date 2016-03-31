package jujuj.demo3;

import com.shinado.netframe.sample.R;

import framework.inj.ActivityInj;
import framework.inj.entity.Multipleable;
import framework.inj.entity.MutableEntity;

@ActivityInj(R.layout.activity_demo2n3)
class Bean implements Multipleable{

	public Bean(String userName){
		UserBean bean = new UserBean();
		bean.userName = userName;
		user = new MutableEntity(bean);
	}

	public MutableEntity user;
	
	public MutableEntity number = new MutableEntity(new Numbers());

	@Override
	public Object[] getLoaders() {
		return new Object[]{user, number};
	}
}
