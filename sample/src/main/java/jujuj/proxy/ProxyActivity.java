package jujuj.proxy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import framework.core.Jujuj;
import framework.inj.entity.utility.Notifiable;

public class ProxyActivity extends Activity implements Notifiable{

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

	@Override
	public void onDownloadResponse() {
		dismissDialog();
	}

	@Override
	public void onError(String msg) {
		dismissDialog();
	}
}
