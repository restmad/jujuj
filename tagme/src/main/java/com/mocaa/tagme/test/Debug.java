package com.mocaa.tagme.test;

import android.util.Log;

public class Debug {

	private final static String TAG = "tag.debug";
	
	public static void o(String msg){
		Log.v(TAG, msg);
	}
}
