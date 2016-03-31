package jujuj.demo3;

import android.app.Activity;
import android.os.Bundle;

import framework.core.Jujuj;

public class Demo3Activity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		Jujuj.getInstance().inject(this, new Bean("Dan"));
	}
	
}
