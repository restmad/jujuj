package demo4;

import com.shinado.netframe.sample.R;

import java.util.ArrayList;

import framework.inj.GroupViewInj;
import framework.inj.ViewInj;


@GroupViewInj(R.layout.layout_demo2n3_number)
class Numbers{

	@ViewInj(R.id.contact_number)
	public String number;

}
