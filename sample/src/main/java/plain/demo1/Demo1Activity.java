package plain.demo1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.shinado.netframe.sample.R;

import java.util.HashMap;

import framework.provider.Listener;
import jujuj.downloadable.Demo2Activity;
import provider.volley.VolleyProvider;
import sample.Constants;

public class Demo1Activity extends Activity{

	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo1);

		findViewById(R.id.login_submit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
				HashMap<String, String> params = new HashMap<>();

				EditText accountInput = (EditText) findViewById(R.id.login_account);
				params.put("account", accountInput.getText().toString());

				EditText pwdInput = (EditText) findViewById(R.id.login_pwd);
				params.put("pwd", pwdInput.getText().toString());

				new VolleyProvider().handleData(Demo1Activity.this,
						Constants.URL + "netframe_sign_in.php",
						params,
						Result.class,
						new Listener.Response<Result>() {
							@Override
							public void onResponse(Result obj) {
								dismissDialog();
								if(obj.id > 0){
									startActivity(new Intent(Demo1Activity.this, Demo2Activity.class));
								}else{
									Toast.makeText(Demo1Activity.this, "login failed", Toast.LENGTH_LONG).show();
								}
							}
						},
						new Listener.Error() {
							@Override
							public void onError(String msg) {
								dismissDialog();
								Toast.makeText(Demo1Activity.this, msg, Toast.LENGTH_LONG).show();
							}
						}
				);
			}
		});
	}

	public void showDialog(){
		if(dialog == null){
			dialog = new ProgressDialog(this);
		}
		dialog.show();
	}

	public void dismissDialog(){
		if(dialog != null){
			dialog.dismiss();
		}
	}

	public class Result{
		public int id;
	}
}
