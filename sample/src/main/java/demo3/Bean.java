package demo3;

import com.shinado.netframe.sample.R;

import framework.inj.ActivityInj;
import framework.inj.entity.Multipleable;
import framework.inj.entity.MutableEntity;

@ActivityInj(R.layout.activity_demo2n3)
public class Bean implements Multipleable{

	public MutableEntity user = new MutableEntity(new UserBean());
	
	public MutableEntity number = new MutableEntity(new Numbers());
	
}
