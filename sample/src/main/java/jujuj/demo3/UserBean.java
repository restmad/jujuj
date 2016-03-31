package jujuj.demo3;

import android.content.Context;

import com.shinado.netframe.sample.R;

import java.util.HashMap;

import framework.inj.ViewInj;
import framework.inj.entity.Downloadable;
import sample.Constants;

public class UserBean implements Downloadable{
	
	@ViewInj(R.id.user_portrait)
	public String userPortrait;

	@ViewInj(R.id.user_name)
	public String userName;

	@ViewInj(R.id.email)
	public String email;

	@ViewInj(R.id.married)
	public boolean married;

	@Override
	public String onDownLoadUrl(Context context) {
		return Constants.URL + "netframe_get_only_user.php";
	}

	@Override
	public void onDownLoadResponse(Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object onDownloadParams() {
		HashMap<String, String> params = new HashMap<>();
		params.put("userName", userName);
		return params;
	}

	@Override
	public void onError(Context context, String msg) {

	}

}
