package sample;

import org.json.JSONException;
import org.json.JSONObject;
 
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shinado.netframe.sample.R;
import com.uni.netframe.Netframe;

import framework.inj.GroupViewInj;
import framework.inj.ViewInj;
import framework.inj.entity.MutableEntity;
import framework.inj.entity.Postable;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class LoginActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
		Netframe.getInstance().inject(this, new MutableEntity(new LoginRequest()));
	}

	@GroupViewInj(R.layout.activity_login)
	public class LoginRequest implements Postable{
		
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
		public void onPostResponse(Context context, JSONObject obj) {
			if(dialog != null){
				dialog.dismiss();
			}
			try {
				int id = obj.getInt("id");
				if(id > 0){
					startActivity(new Intent(LoginActivity.this, MainActivity.class));
				}else{
					Toast.makeText(context, "login failed", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public String onPostUrl(Context context) {
			if(dialog == null){
				dialog = new ProgressDialog(context);
			}
			dialog.show();
			return MyApplication.URL + "netframe_sign_in.php";
		}

		@Override
		public void onError(Context context, String msg) {

		}

	}
}
