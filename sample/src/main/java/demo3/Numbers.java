package demo3;

import java.util.ArrayList;

import framework.inj.entity.Downloadable;
import sample.MyApplication;
import android.content.Context;
import android.util.Log;

import com.shinado.netframe.sample.R;

import framework.inj.GroupViewInj;
import framework.inj.ViewInj;
import framework.inj.entity.ArrayDownloadable;

public class Numbers implements Downloadable{
	
	@ViewInj(R.id.number_list)
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
		Log.d("HttpRequest", "number error:"+msg);
	}

	@GroupViewInj(R.layout.layout_demo2n3_number)
	public class Number{
		@ViewInj(R.id.contact_number)
		public String number;
	}
}
