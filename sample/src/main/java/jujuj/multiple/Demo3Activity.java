package jujuj.multiple;

import android.app.Activity;
import android.os.Bundle;

import framework.core.Jujuj;

//test for multiple
public class Demo3Activity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		Jujuj.getInstance().inject(this, new Bean("Dan"));
	}
	
}
