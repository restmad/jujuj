package demo2;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.shinado.netframe.sample.R;

import framework.inj.GroupViewInj;
import framework.inj.ViewInj;
import provider.database.Entity;

@GroupViewInj(R.layout.layout_demo2n3_number)
@Table(name = "Numbers")
class Number extends Entity {
	
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
	public void setForeignKey(Entity entity) {
		user = (UserBean) entity;
	}

}
