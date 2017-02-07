package jujuj.validate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import framework.core.Jujuj;

public class ValidateActivity extends Activity{

	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Jujuj.getInstance().inject(this, new LoginRequest(this));
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

}
