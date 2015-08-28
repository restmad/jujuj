package demo2;

import java.util.HashMap;
import java.util.List;

import sample.MyApplication;
import android.content.Context;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.shinado.netframe.sample.R;

import framework.inj.ActivityInj;
import framework.inj.ViewInj;
import framework.inj.entity.Downloadable;
import framework.inj.entity.Entity;


@ActivityInj(R.layout.activity_demo2n3)
@Table(name = "User")
public class UserBean extends Entity implements Downloadable{
	
	@ViewInj(R.id.user_portrait)
	@Column(name = "portrait")
	public String userPortrait;

	@ViewInj(R.id.user_name)
	@Column(name = "userName")
	public String userName;

	@ViewInj(R.id.email)
	@Column(name = "email")
	public String email;

	@ViewInj(R.id.married)
	@Column(name = "married")
	public boolean married;

	@ViewInj(R.id.number_list)
	public List<Number> numbers;

	public List<Number> numbers(){
		//WARNING don't use getMany, use getMani instead
		return getMani(Number.class, "userBean");
	}
	
	public UserBean(){
		super();  
	}
	
	@Override
	public Entity query() {
		UserBean entity = new Select().from(UserBean.class).where("userName = ?", userName).executeSingle();
		if(entity != null){
			entity.numbers = entity.numbers();
		} 
		return entity;
	}

	@Override
	public String onDownLoadUrl(Context context) {
		return MyApplication.URL + "netframe_get_user.php";
	}

	@Override
	public void onDownLoadResponse(Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object onDownloadParams() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("userName", userName);
		return params;
	}

	@Override
	public void onError(Context context, String msg) {

	}

}
