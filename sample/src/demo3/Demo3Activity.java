package demo3;

import android.app.Activity;
import android.os.Bundle;

import com.uni.netframe.Netframe;

public class Demo3Activity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		Netframe.inject(this, new Bean());
	}
	
}
