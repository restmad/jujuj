package demo3;

import java.util.ArrayList;

import sample.MyApplication;
import android.content.Context;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.shinado.netframe.sample.R;

import framework.inj.GroupViewInj;
import framework.inj.ViewInj;
import framework.inj.entity.ArrayDownloadable;
import framework.inj.entity.Downloadable;
import framework.inj.entity.Entity;

public class Numbers implements ArrayDownloadable{
	
	@ViewInj(R.id.contact_number)
	public ArrayList<Number> numbers;

	@Override
	public String onDownLoadUrl(Context context) {
		return MyApplication.URL + "netframe_get_user_numbs.php";
	}

	@Override
	public void onDownLoadResponse(Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object onDownloadParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onError(Context context, String msg) {

	}

	@GroupViewInj(R.layout.layout_number)
	class Number{
		@ViewInj(R.id.contact_number)
		String number;
	}
}
