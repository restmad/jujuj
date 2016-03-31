package jujuj.demo3;

import android.content.Context;
import android.util.Log;

import com.shinado.netframe.sample.R;

import java.util.ArrayList;

import framework.inj.GroupViewInj;
import framework.inj.ViewInj;
import framework.inj.entity.Downloadable;
import sample.Constants;

class Numbers implements Downloadable{
	
	@ViewInj(R.id.number_list)
	public ArrayList<Number> numbers;

	@Override
	public String onDownLoadUrl(Context context) {
		return Constants.URL + "netframe_get_user_numbs.php";
	}

	@Override
	public void onDownLoadResponse(Context context) {
	}

	@Override
	public Object onDownloadParams() {
		return null;
	}

	@Override
	public void onError(Context context, String msg) {
	}

	@GroupViewInj(R.layout.layout_demo2n3_number)
	public class Number{
		@ViewInj(R.id.contact_number)
		public String number;
	}
}
