package demo1;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.shinado.netframe.sample.R;

import demo2.Demo2Activity;
import framework.core.Jujuj;
import framework.inj.ActivityInj;
import framework.inj.ViewInj;
import framework.inj.entity.MutableEntity;
import framework.inj.entity.Postable;
import sample.Constants;

public class Demo1Activity extends Activity{

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Jujuj.getInstance().inject(this, new MutableEntity(new LoginRequest()));
	}

	@ActivityInj(R.layout.activity_demo1)
	public class LoginRequest implements Postable<LoginRequest.Result> {
		
		private ProgressDialog dialog;
		
		@ViewInj(R.id.login_account)
		public String account;

		@ViewInj(R.id.login_pwd)
		public String pwd;

		@Override
		public int getSubmitButtonId() {
			return R.id.login_submit;
		}

		@Override
		public void onPostResponse(Context context, LoginRequest.Result result) {
			if(dialog != null){
				dialog.dismiss();
			}
			if(result.id > 0){
				startActivity(new Intent(Demo1Activity.this, Demo2Activity.class));
			}else{
				Toast.makeText(context, "login failed", Toast.LENGTH_LONG).show();
			}
		}

		public class Result{
			public int id;
		}

		@Override
		public String onPostUrl(Context context) {
			if(dialog == null){
				dialog = new ProgressDialog(context);
			}
			dialog.show();
			return Constants.URL + "netframe_sign_in.php";
		}

		@Override
		public void onError(Context context, String msg) {

		}

	}
}
