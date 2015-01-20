package sample;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.shinado.netframe.sample.R;

import framework.inj.ViewGroupInj;
import framework.inj.ViewInj;
import framework.inj.entity.Entity;

@ViewGroupInj(R.layout.layout_number)
@Table(name = "Number")
public class Number extends Entity{
	
	public Number(){
		super();
	}
	
	@ViewInj(R.id.contact_number)
	@Column(name = "number")
	public String number;

	@Column(name = "userBean")
	public UserBean user;

	@Override
	public Entity query() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setForignKey(Entity entity) {
		user = (UserBean) entity;
	}

}
